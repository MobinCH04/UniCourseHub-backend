package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.CourseOffering;
import com.mch.unicoursehub.model.entity.Course;
import com.mch.unicoursehub.model.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link CourseOffering} entity.
 *
 * <p>
 * Provides methods for CRUD operations and custom queries related to course offerings,
 * including filtering by course, semester, section, and checking existence.
 * </p>
 */
public interface CourseOfferingRepository extends JpaRepository<CourseOffering, UUID> {

    /**
     * Counts the number of course offerings for a given course and semester.
     *
     * @param course the course entity
     * @param semester the semester entity
     * @return the number of course offerings for the specified course and semester
     */
    int countByCourseAndSemester(Course course, Semester semester);

    /**
     * Finds all course offerings in a given semester.
     *
     * @param semester the semester entity
     * @return list of course offerings in the specified semester
     */
    List<CourseOffering> findBySemester(Semester semester);

    /**
     * Finds a course offering by course code and section.
     *
     * @param courseCode the code of the course
     * @param section the section number
     * @return optional course offering matching the criteria
     */
    Optional<CourseOffering> findByCourse_CodeAndSection(
            String courseCode,
            int section
    );

    /**
     * Finds a course offering by course code, section, and semester name.
     *
     * @param courseCode the code of the course
     * @param section the section number
     * @param semesterName the name of the semester
     * @return optional course offering matching all criteria
     */
    Optional<CourseOffering> findByCourse_CodeAndSectionAndSemester_Name(
            String courseCode,
            int section,
            String semesterName
    );

    /**
     * Checks if a course offering exists for a given course and semester,
     * excluding a specific offering by its ID.
     *
     * @param course the course entity
     * @param semester the semester entity
     * @param id the ID of the course offering to exclude
     * @return true if such a course offering exists, false otherwise
     */
    boolean existsByCourseAndSemesterAndIdNot(
            Course course,
            Semester semester,
            UUID id
    );
}