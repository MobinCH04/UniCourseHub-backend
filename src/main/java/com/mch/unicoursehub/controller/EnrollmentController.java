package com.mch.unicoursehub.controller;

import com.mch.unicoursehub.model.dto.DropCourseRequest;
import com.mch.unicoursehub.model.dto.EnrollCourseRequest;
import com.mch.unicoursehub.model.dto.StudentEnrollmentResponse;
import com.mch.unicoursehub.model.entity.User;
import com.mch.unicoursehub.service.EnrollmentService;
import com.mch.unicoursehub.service.impl.EnrollmentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing student course enrollments.
 *
 * <p>
 * Provides endpoints for students to enroll in courses, view their
 * enrolled courses, and drop courses.
 * </p>
 */
@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    /**
     * Service responsible for enrollment-related business logic.
     */
    private final EnrollmentServiceImpl enrollmentServiceImpl;

    /**
     * Enrolls a student in a course for a specific semester.
     *
     * <p>
     * The endpoint is intended for authenticated students. The
     * request body should contain the necessary information to
     * enroll in the course.
     * </p>
     *
     * @param student      the authenticated student
     * @param semesterName the semester to enroll in
     * @param req          enrollment request data
     */
    @Operation(summary = "Taking course.", description = "This route can be used by student.")
    @PostMapping("/{semesterName}")
    @ResponseStatus(HttpStatus.OK)
    public void enroll(
            @AuthenticationPrincipal User student,
            @PathVariable String semesterName,
            @RequestBody @Valid EnrollCourseRequest req
            ) {
        enrollmentServiceImpl.enrollStudent(student,semesterName, req);
    }

    /**
     * Retrieves all courses that a student is enrolled in for a specific semester.
     *
     * <p>
     * The endpoint returns a list of enrolled courses for the authenticated student.
     * </p>
     *
     * @param student  the authenticated student
     * @param semester the semester to filter enrollments by
     * @return list of student's enrolled courses
     */
    @Operation(summary = "Courses taken by the student.")
    @GetMapping
    private ResponseEntity<List<StudentEnrollmentResponse>> getStudentEnrollments(
            @AuthenticationPrincipal User student,
            @RequestParam String semester
    ){
        return ResponseEntity.ok(enrollmentServiceImpl.getStudentEnrollments(student, semester));
    }

    /**
     * Drops a course that the student is currently enrolled in.
     *
     * <p>
     * The endpoint allows authenticated students to drop a course
     * using the information provided in the request body.
     * </p>
     *
     * @param student the authenticated student
     * @param req     drop course request data
     */
    @Operation(summary = "Dropped course by the student.")
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void dropCourse(@AuthenticationPrincipal User student, @RequestBody @Valid DropCourseRequest req) {
        enrollmentServiceImpl.dropCourse(student, req);
    }
}
