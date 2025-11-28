package com.mch.unicoursehub.service.impl;


import com.mch.unicoursehub.exceptions.BadRequestException;
import com.mch.unicoursehub.exceptions.ConflictException;
import com.mch.unicoursehub.model.dto.CourseResponse;
import com.mch.unicoursehub.model.dto.CreateCourseRequest;
import com.mch.unicoursehub.model.entity.Course;
import com.mch.unicoursehub.model.entity.Prerequisite;
import com.mch.unicoursehub.repository.CourseRepository;
import com.mch.unicoursehub.repository.PrerequisiteRepository;
import com.mch.unicoursehub.service.CourseService;
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
}
