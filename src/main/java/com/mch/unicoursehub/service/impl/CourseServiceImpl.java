package com.mch.unicoursehub.service.impl;


import com.mch.unicoursehub.exceptions.BadRequestException;
import com.mch.unicoursehub.exceptions.ConflictException;
import com.mch.unicoursehub.exceptions.NotFoundException;
import com.mch.unicoursehub.model.dto.AllCoursesResponse;
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

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final PrerequisiteRepository prerequisiteRepository;

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
     * Detect cycles in prerequisites graph involving the newly created course.
     * We do a DFS from the saved course following "dependentCourses" edges.
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

    @Override
    @Transactional(readOnly = true)
    public Pagination<AllCoursesResponse> getAllCourses(int page, int size, String code, String name, Integer unit) {


        List<Course> allCourses = courseRepository.findAll();


        List<Course> filtered = allCourses.stream()
                .filter(c -> code == null || c.getCode().equalsIgnoreCase(code.trim()))
                .filter(c -> name == null || c.getName().toLowerCase().contains(name.trim().toLowerCase()))
                .filter(c -> unit == null || c.getUnit() == unit)
                .toList();


        List<AllCoursesResponse> dtoList = filtered.stream()
                .map(c -> new AllCoursesResponse(
                        c.getCode(),
                        c.getName(),
                        c.getUnit()
                ))
                .toList();


        return PaginationUtil.pagination(dtoList, page, size);
    }

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
}
