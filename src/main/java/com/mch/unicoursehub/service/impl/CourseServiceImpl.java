package com.mch.unicoursehub.service.impl;


import com.mch.unicoursehub.exceptions.BadRequestException;
import com.mch.unicoursehub.exceptions.ConflictException;
import com.mch.unicoursehub.exceptions.NotFoundException;
import com.mch.unicoursehub.model.dto.CourseResponse;
import com.mch.unicoursehub.model.dto.CreateCourseRequest;
import com.mch.unicoursehub.model.dto.UpdateCourseRequest;
import com.mch.unicoursehub.model.entity.Course;
import com.mch.unicoursehub.model.entity.Prerequisite;
import com.mch.unicoursehub.repository.CourseRepository;
import com.mch.unicoursehub.repository.PrerequisiteRepository;
import com.mch.unicoursehub.service.CourseService;
import com.mch.unicoursehub.utils.pagination.Pagination;
import com.mch.unicoursehub.utils.pagination.PaginationUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for managing course-related operations.
 *
 * <p>This class provides the core business logic for creating, retrieving,
 * updating, and deleting courses within the system. It also manages
 * prerequisite relationships between courses and ensures data integrity
 * by preventing invalid or cyclic prerequisite dependencies.</p>
 *
 * <p>The service interacts with {@link CourseRepository} and
 * {@link PrerequisiteRepository} for persistence operations and enforces
 * domain-level validation rules such as uniqueness of course codes,
 * existence of prerequisite courses, and acyclic prerequisite graphs.</p>
 *
 * <p>All write operations are transactional to guarantee consistency
 * across course and prerequisite entities.</p>
 *
 * @see CourseService
 * @see Course
 * @see Prerequisite
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final PrerequisiteRepository prerequisiteRepository;

    /**
     * Creates a new course along with its prerequisite relationships.
     *
     * <p>This method validates the uniqueness of the course code, verifies
     * that all provided prerequisite course codes exist, and prevents
     * self-referencing or cyclic prerequisite definitions.</p>
     *
     * <p>The course entity is first persisted to generate its identifier,
     * after which prerequisite relationships are stored. A cycle detection
     * check is performed before completing the operation.</p>
     *
     * @param req request object containing course details and prerequisite codes
     * @return a {@link CourseResponse} representing the created course
     *
     * @throws ConflictException if a course with the same code already exists
     * @throws BadRequestException if prerequisite validation fails or a cycle is detected
     */
    @Override
    @Transactional
    public CourseResponse createCourse(CreateCourseRequest req) {

        String code = req.code().trim();
        String name = req.name().trim();
        int unit = req.unit();

        if (courseRepository.existsByCode(code)) {
            throw new ConflictException("course with code '" + code + "' already exists");
        }

        // build Course entity via builder
        Course course = Course.builder()
                .code(code)
                .name(name)
                .unit(unit)
                .prerequisites(new ArrayList<>())
                .dependentCourses(new ArrayList<>())
                .offerings(new ArrayList<>())
                .build();

        // prerequisite codes
        List<String> prereqCodes = Optional.ofNullable(req.prerequisiteCodes())
                .orElse(List.of())
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        List<Course> prereqCourses = List.of();

        if (!prereqCodes.isEmpty()) {
            prereqCourses = courseRepository.findByCodeIn(prereqCodes);

            Set<String> found = prereqCourses.stream().map(Course::getCode).collect(Collectors.toSet());
            List<String> notFound = prereqCodes.stream().filter(c -> !found.contains(c)).toList();

            if (!notFound.isEmpty()) {
                throw new BadRequestException("prerequisite courses not found: " + notFound);
            }

            if (prereqCodes.contains(code)) {
                throw new BadRequestException("course cannot be prerequisite of itself");
            }
        }

        // save to generate cid
        Course saved = courseRepository.save(course);

        // save prerequisite relations
        if (!prereqCourses.isEmpty()) {
            List<Prerequisite> toSave = prereqCourses.stream()
                    .map(p -> Prerequisite.builder()
                            .course(saved)
                            .prerequisite(p)
                            .build())
                    .toList();

            prerequisiteRepository.saveAll(toSave);
            saved.setPrerequisites(toSave);
        }

        // cycle detection
        if (createsCycle(saved)) {
            throw new BadRequestException("adding these prerequisites introduces cyclic dependency");
        }

        return new CourseResponse(
                saved.getCode(),
                saved.getName(),
                saved.getUnit(),
                prereqCourses.stream().map(Course::getCode).toList()
        );
    }


    /**
     * Detects whether the current prerequisite configuration introduces
     * a cyclic dependency among courses.
     *
     * <p>This method constructs a directed graph of all courses and their
     * prerequisites from the database and performs a depth-first search (DFS)
     * to identify cycles.</p>
     *
     * @param newCourse the course being created or updated
     * @return {@code true} if a cyclic dependency exists, {@code false} otherwise
     */
    private boolean createsCycle(Course newCourse) {
        // Build adjacency from DB (courses -> their prerequisites' codes)
        List<Course> all = courseRepository.findAll();
        Map<UUID, List<UUID>> adj = new HashMap<>();
        for (Course c : all) {
            List<UUID> prereqIds = c.getPrerequisites().stream()
                    .map(pr -> pr.getPrerequisite().getCid())
                    .collect(Collectors.toList());
            adj.put(c.getCid(), prereqIds);
        }

        // detect cycle using DFS
        Set<UUID> visiting = new HashSet<>();
        Set<UUID> visited = new HashSet<>();

        for (UUID node : adj.keySet()) {
            if (hasCycle(node, adj, visiting, visited)) return true;
        }
        return false;
    }

    /**
     * Recursive helper method for detecting cycles in a directed graph
     * using depth-first search.
     *
     * @param node current course identifier being visited
     * @param adj adjacency list representing prerequisite relationships
     * @param visiting set of nodes currently in the DFS recursion stack
     * @param visited set of nodes that have been fully processed
     * @return {@code true} if a cycle is detected, {@code false} otherwise
     */
    private boolean hasCycle(UUID node, Map<UUID, List<UUID>> adj, Set<UUID> visiting, Set<UUID> visited) {
        if (visited.contains(node)) return false;
        if (visiting.contains(node)) return true;
        visiting.add(node);
        for (UUID nei : adj.getOrDefault(node, Collections.emptyList())) {
            if (hasCycle(nei, adj, visiting, visited)) return true;
        }
        visiting.remove(node);
        visited.add(node);
        return false;
    }

    /**
     * Retrieves a paginated list of courses with optional filtering.
     *
     * <p>Filtering can be applied based on course code, course name,
     * and unit count. Results are mapped to DTOs and paginated in-memory
     * using {@link PaginationUtil}.</p>
     *
     * @param page page number (zero-based)
     * @param size number of records per page
     * @param code optional exact course code filter
     * @param name optional partial course name filter
     * @param unit optional unit count filter
     * @return a {@link Pagination} object containing {@link CourseResponse} entries
     */
    @Override
    @Transactional(readOnly = true)
    public Pagination<CourseResponse> getAllCourses(int page, int size, String code, String name, Integer unit) {


        List<Course> allCourses = courseRepository.findAll();


        List<Course> filtered = allCourses.stream()
                .filter(c -> code == null || c.getCode().equalsIgnoreCase(code.trim()))
                .filter(c -> name == null || c.getName().toLowerCase().contains(name.trim().toLowerCase()))
                .filter(c -> unit == null || c.getUnit() == unit)
                .toList();


        List<CourseResponse> dtoList = filtered.stream()
                .map(c -> new CourseResponse(
                        c.getCode(),
                        c.getName(),
                        c.getUnit(),
                        c.getPrerequisites()
                                .stream()
                                .map(pr -> pr.getPrerequisite().getCode())
                                .toList()
                ))
                .toList();


        return PaginationUtil.pagination(dtoList, page, size);
    }

    /**
     * Updates an existing course and its prerequisite relationships.
     *
     * <p>This method allows partial updates to the course name, unit,
     * and prerequisites. When prerequisites are updated, existing
     * prerequisite relationships are removed and replaced.</p>
     *
     * <p>After applying changes, a cycle detection check is performed
     * to ensure that the updated configuration does not introduce
     * cyclic dependencies.</p>
     *
     * @param code the unique code of the course to update
     * @param req request object containing updated course data
     * @return a {@link CourseResponse} representing the updated course
     *
     * @throws NotFoundException if the course does not exist
     * @throws BadRequestException if prerequisite validation fails or a cycle is detected
     */
    @Override
    @Transactional
    public CourseResponse updateCourse(String code, UpdateCourseRequest req) {
        // پیدا کردن درس بر اساس code
        Course course = courseRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Course with code '" + code + "' not found"));

        // تغییر نام درس اگر مقدار داده شده باشه
        if (req.name() != null && !req.name().isBlank()) {
            course.setName(req.name().trim());
        }

        // تغییر واحد درس اگر مقدار داده شده باشه
        if (req.unit() != null) {
            course.setUnit(req.unit());
        }

        // بروزرسانی پیش‌نیازها اگر مقدار داده شده باشه
        if (req.prerequisiteCodes() != null) {
            List<String> prereqCodes = req.prerequisiteCodes().stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();

            // بررسی پیش‌نیازها در DB
            List<Course> prereqCourses = List.of();
            if (!prereqCodes.isEmpty()) {
                prereqCourses = courseRepository.findByCodeIn(prereqCodes);

                Set<String> found = prereqCourses.stream().map(Course::getCode).collect(Collectors.toSet());
                List<String> notFound = prereqCodes.stream().filter(c -> !found.contains(c)).toList();

                if (!notFound.isEmpty()) {
                    throw new BadRequestException("prerequisite courses not found: " + notFound);
                }

                if (prereqCodes.contains(code)) {
                    throw new BadRequestException("course cannot be prerequisite of itself");
                }
            }

            // حذف پیش‌نیازهای قدیمی
            prerequisiteRepository.deleteAll(course.getPrerequisites());

            // ذخیره پیش‌نیازهای جدید
            if (!prereqCourses.isEmpty()) {
                List<Prerequisite> toSave = prereqCourses.stream()
                        .map(p -> Prerequisite.builder()
                                .course(course)
                                .prerequisite(p)
                                .build())
                        .toList();
                prerequisiteRepository.saveAll(toSave);
                course.setPrerequisites(toSave);
            }
        }

        // بررسی حلقه پیش‌نیازها
        if (createsCycle(course)) {
            throw new BadRequestException("updating these prerequisites introduces cyclic dependency");
        }

        // ذخیره تغییرات
        Course saved = courseRepository.save(course);

        return new CourseResponse(
                saved.getCode(),
                saved.getName(),
                saved.getUnit(),
                saved.getPrerequisites().stream()
                        .map(pr -> pr.getPrerequisite().getCode())
                        .toList()
        );
    }

    /**
     * Deletes a course and all related prerequisite associations.
     *
     * <p>This operation ensures referential integrity by first removing
     * the course from other courses' prerequisite lists, then deleting
     * its own prerequisite relationships, and finally removing the
     * course entity itself.</p>
     *
     * @param code the unique code of the course to delete
     *
     * @throws NotFoundException if the course does not exist
     */
    @Override
    @Transactional
    public void deleteCourse(String code) {

        // پیدا کردن درس
        Course course = courseRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Course with code '" + code + "' not found"));

        UUID cid = course.getCid();

        // 1) حذف درس از پیش‌نیازهای درس‌های دیگر (اگر درس برای بقیه prerequisite بوده)
        List<Prerequisite> dependentPrereqs = prerequisiteRepository.findByPrerequisiteCid(cid);

        if (!dependentPrereqs.isEmpty()) {
            prerequisiteRepository.deleteAll(dependentPrereqs);
        }

        // 2) حذف پیش‌نیازهای همین درس (course → prereqs)
        if (!course.getPrerequisites().isEmpty()) {
            prerequisiteRepository.deleteAll(course.getPrerequisites());
        }

        // 3) در نهایت حذف خود درس
        courseRepository.delete(course);
    }

}
