package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link Course} entity.
 *
 * <p>
 * Provides methods for CRUD operations and custom queries related to courses,
 * including finding by course code and checking existence.
 * </p>
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    /**
     * Finds a course by its unique code.
     *
     * @param code the code of the course
     * @return an {@link Optional} containing the course if found, or empty if not
     */
    Optional<Course> findByCode(String code);

    /**
     * Checks if a course with the given code exists.
     *
     * @param code the code of the course
     * @return true if a course with the code exists, false otherwise
     */
    boolean existsByCode(String code);

    /**
     * Finds all courses with codes included in the provided list.
     *
     * @param codes a list of course codes
     * @return a list of courses matching the provided codes
     */
    List<Course> findByCodeIn(List<String> codes);
}
