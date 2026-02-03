package com.mch.unicoursehub.controller;

import com.mch.unicoursehub.model.dto.CourseOfferingResponse;
import com.mch.unicoursehub.model.dto.DropEnrollmentRequest;
import com.mch.unicoursehub.model.dto.UserListResponse;
import com.mch.unicoursehub.service.impl.ProfessorServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for professor-related operations.
 *
 * <p>
 * Provides endpoints for professors to view their course offerings,
 * get students enrolled in their courses, and remove students from
 * course offerings they manage.
 * </p>
 */
@RestController
@RequestMapping("/professor")
@RequiredArgsConstructor
@Tag(name = "Professor", description = "Professor operations")
public class ProfessorController {

    /**
     * Service responsible for professor-related business logic.
     */
    private final ProfessorServiceImpl professorServiceImpl;

    /**
     * Retrieves all course offerings assigned to the currently logged-in professor.
     *
     * @return a list of course offerings managed by the professor
     */
    @Operation(summary = "Get course offerings assigned to the currently logged-in professor")
    @GetMapping("/course-offerings")
    public ResponseEntity<List<CourseOfferingResponse>> getMyCourseOfferings(
            @RequestParam String semesterName
    ) {
        List<CourseOfferingResponse> offerings = professorServiceImpl.getMyCourseOfferings(semesterName);
        return ResponseEntity.ok(offerings);
    }

    /**
     * Retrieves all students enrolled in a specific course offering assigned to the logged-in professor.
     *
     * @param courseCode  the code of the course
     * @param groupNumber the group number of the course offering
     * @param semesterName the semester of the course offering
     * @return a list of students enrolled in the specified course offering
     */
    @Operation(summary = "Get students of a course offering assigned to the logged-in professor")
    @GetMapping("/course-offerings/students")
    public ResponseEntity<List<UserListResponse>> getStudentsOfCourseOffering(
            @RequestParam String courseCode,
            @RequestParam int groupNumber,
            @RequestParam String semesterName) {

        List<UserListResponse> students = professorServiceImpl.getStudentsOfOfferingByKeys(
                courseCode.trim(), groupNumber, semesterName.trim()
        );
        return ResponseEntity.ok(students);
    }

    /**
     * Removes a student from a course offering.
     *
     * <p>
     * The professor must own the course offering in order to remove a student.
     * The request body must include the necessary information to identify the student.
     * </p>
     *
     * @param semesterName the semester of the course offering
     * @param req          request containing student enrollment details to remove
     */
    @Operation(summary = "Remove a student from a course offering (professor must own the offering)")
    @DeleteMapping("/course-offerings/students")
    public void removeStudentFromCourseOffering(@RequestParam String semesterName,
                                                @RequestBody @Valid DropEnrollmentRequest req) {
        professorServiceImpl.removeStudentFromOffering(semesterName.trim(),req);
    }
}