package com.mch.unicoursehub.repository;

import com.mch.unicoursehub.model.entity.*;
import com.mch.unicoursehub.model.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    long countByCourseOffering(CourseOffering offering);

    boolean existsByStudentAndCourseOffering_SemesterAndCourseOffering_Course(
            User student,
            Semester semester,
            Course course
    );

    List<Enrollment> findByStudentAndCourseOffering_Semester(
            User student,
            Semester semester
    );

    List<Enrollment> findByStudentAndStatus(
            User student,
            EnrollmentStatus status
    );

    boolean existsByStudentAndCourseOffering_CourseAndStatus(
            User student,
            Course course,
            EnrollmentStatus status
    );

    Optional<Enrollment> findByStudentAndCourseOffering_Course_CodeAndCourseOffering_SectionAndCourseOffering_Semester_Name(
            User student,
            String courseCode,
            int section,
            String semesterName
    );

    boolean existsByStudentAndCourseOffering_SemesterAndCourseOffering_CourseAndStatus(
            User student,
            Semester semester,
            Course course,
            EnrollmentStatus status
    );

    Optional<Enrollment> findByStudentAndCourseOffering(
            User student,
            CourseOffering offering
    );

    boolean existsByCourseOfferingAndStatusNot(
            CourseOffering offering,
            EnrollmentStatus status
    );

    void deleteByCourseOffering(CourseOffering offering);

    List<Enrollment> findByStudentAndCourseOffering_SemesterAndStatus(
            User student,
            Semester semester,
            EnrollmentStatus status
    );

}
