package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.*;
import com.mch.unicoursehub.model.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link Enrollment} entity.
 *
 * <p>
 * Provides methods for CRUD operations and custom queries related to student enrollments,
 * including filtering by student, course, semester, and enrollment status.
 * </p>
 */
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    /**
     * Counts the number of enrollments for a specific course offering.
     *
     * @param offering the course offering entity
     * @return the total number of enrollments for the offering
     */
    long countByCourseOffering(CourseOffering offering);

    /**
     * Checks if a student has any enrollment for a specific course in a specific semester.
     *
     * @param student the student entity
     * @param semester the semester entity
     * @param course the course entity
     * @return true if such enrollment exists, false otherwise
     */
    boolean existsByStudentAndCourseOffering_SemesterAndCourseOffering_Course(
            User student,
            Semester semester,
            Course course
    );

    /**
     * Finds all enrollments of a student in a specific semester.
     *
     * @param student the student entity
     * @param semester the semester entity
     * @return list of enrollments for the student in the semester
     */
    List<Enrollment> findByStudentAndCourseOffering_Semester(
            User student,
            Semester semester
    );

    /**
     * Finds all enrollments of a student with a specific enrollment status.
     *
     * @param student the student entity
     * @param status the enrollment status
     * @return list of enrollments matching the criteria
     */
    List<Enrollment> findByStudentAndStatus(
            User student,
            EnrollmentStatus status
    );

    /**
     * Checks if a student has an enrollment in a course with a specific status.
     *
     * @param student the student entity
     * @param course the course entity
     * @param status the enrollment status
     * @return true if such enrollment exists, false otherwise
     */
    boolean existsByStudentAndCourseOffering_CourseAndStatus(
            User student,
            Course course,
            EnrollmentStatus status
    );

    /**
     * Finds an enrollment for a student by course code, section, and semester name.
     *
     * @param student the student entity
     * @param courseCode the course code
     * @param section the section number
     * @param semesterName the semester name
     * @return optional enrollment matching all criteria
     */
    Optional<Enrollment> findByStudentAndCourseOffering_Course_CodeAndCourseOffering_SectionAndCourseOffering_Semester_Name(
            User student,
            String courseCode,
            int section,
            String semesterName
    );

    /**
     * Checks if a student has an enrollment for a course in a semester with a specific status.
     *
     * @param student the student entity
     * @param semester the semester entity
     * @param course the course entity
     * @param status the enrollment status
     * @return true if such enrollment exists, false otherwise
     */
    boolean existsByStudentAndCourseOffering_SemesterAndCourseOffering_CourseAndStatus(
            User student,
            Semester semester,
            Course course,
            EnrollmentStatus status
    );

    /**
     * Finds an enrollment for a student in a specific course offering.
     *
     * @param student the student entity
     * @param offering the course offering entity
     * @return optional enrollment
     */
    Optional<Enrollment> findByStudentAndCourseOffering(
            User student,
            CourseOffering offering
    );

    /**
     * Checks if there are any enrollments in a course offering that do not have a specific status.
     *
     * @param offering the course offering entity
     * @param status the enrollment status to exclude
     * @return true if any enrollment exists with status not equal to the given one, false otherwise
     */
    boolean existsByCourseOfferingAndStatusNot(
            CourseOffering offering,
            EnrollmentStatus status
    );

    /**
     * Deletes all enrollments associated with a specific course offering.
     *
     * @param offering the course offering entity
     */
    void deleteByCourseOffering(CourseOffering offering);

    /**
     * Finds all enrollments of a student in a specific semester with a specific status.
     *
     * @param student the student entity
     * @param semester the semester entity
     * @param status the enrollment status
     * @return list of enrollments matching the criteria
     */
    List<Enrollment> findByStudentAndCourseOffering_SemesterAndStatus(
            User student,
            Semester semester,
            EnrollmentStatus status
    );

}
