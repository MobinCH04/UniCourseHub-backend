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

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentServiceImpl enrollmentServiceImpl;
    @Operation(summary = "Taking course.", description = "This route can be used by student.")
    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public void enroll(
            @AuthenticationPrincipal User student,
            @RequestBody @Valid EnrollCourseRequest req
            ) {
        enrollmentServiceImpl.enrollStudent(student, req);
    }

    @Operation(summary = "Courses taken by the student.")
    @GetMapping
    private ResponseEntity<List<StudentEnrollmentResponse>> getStudentEnrollments(
            @AuthenticationPrincipal User student,
            @RequestParam String semester
    ){
        return ResponseEntity.ok(enrollmentServiceImpl.getStudentEnrollments(student, semester));
    }

    @Operation(summary = "Dropped course by the student.")
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void dropCourse(@AuthenticationPrincipal User student, @RequestBody @Valid DropCourseRequest req) {
        enrollmentServiceImpl.dropCourse(student, req);
    }
}
