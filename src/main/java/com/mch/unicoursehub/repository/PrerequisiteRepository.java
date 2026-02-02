package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.Course;
import com.mch.unicoursehub.model.entity.Prerequisite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link Prerequisite} entity.
 *
 * <p>
 * Provides methods to perform CRUD operations and custom queries related to course prerequisites.
 * This includes retrieving prerequisites of a course and courses that depend on a specific prerequisite.
 * </p>
 */
@Repository
public interface PrerequisiteRepository extends JpaRepository<Prerequisite, UUID> {

    /**
     * Finds all prerequisite relations where the given course is listed as a prerequisite.
     * In other words, retrieves all courses that depend on the course with the given id.
     *
     * @param cid the UUID of the prerequisite course
     * @return list of {@link Prerequisite} entities where the course is a prerequisite
     */
    List<Prerequisite> findByPrerequisiteCid(UUID cid);

    /**
     * Finds all prerequisite relations for a given course.
     * In other words, retrieves all prerequisites that the given course requires.
     *
     * @param course the {@link Course} entity
     * @return list of {@link Prerequisite} entities representing the course's prerequisites
     */
    List<Prerequisite> findByCourse(Course course);
}
