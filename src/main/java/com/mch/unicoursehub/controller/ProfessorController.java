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

@RestController
@RequestMapping("/professor")
@RequiredArgsConstructor
@Tag(name = "Professor", description = "Professor operations")
public class ProfessorController {

    private final ProfessorServiceImpl professorServiceImpl;

    @Operation(summary = "Get course offerings assigned to the currently logged-in professor")
    @GetMapping("/course-offerings")
    public ResponseEntity<List<CourseOfferingResponse>> getMyCourseOfferings() {
        List<CourseOfferingResponse> offerings = professorServiceImpl.getMyCourseOfferings();
        return ResponseEntity.ok(offerings);
    }

    @Operation(summary = "Get students of a course offering assigned to the logged-in professor")
    // Query params used so frontend does not need UUIDs
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

    @Operation(summary = "Remove a student from a course offering (professor must own the offering)")
    @DeleteMapping("/course-offerings/students")
    public void removeStudentFromCourseOffering(@RequestParam String semesterName,
                                                @RequestBody @Valid DropEnrollmentRequest req) {
        professorServiceImpl.removeStudentFromOffering(semesterName.trim(),req);
    }
}