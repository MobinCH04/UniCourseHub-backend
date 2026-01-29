package com.mch.unicoursehub.controller;

import com.mch.unicoursehub.model.dto.CourseOfferingResponse;
import com.mch.unicoursehub.model.dto.UserListResponse;
import com.mch.unicoursehub.service.impl.ProfessorServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    @GetMapping("/course-offerings/{courseOfferingId}/students")
    public ResponseEntity<List<UserListResponse>> getStudentsOfCourseOffering(
            @PathVariable UUID courseOfferingId) {

        List<UserListResponse> students = professorServiceImpl.getStudentsOfCourseOffering(courseOfferingId);
        return ResponseEntity.ok(students);
    }

    @Operation(summary = "Remove a student from a course offering (professor must own the offering)")
    @DeleteMapping("/course-offerings/{courseOfferingId}/students/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeStudentFromCourseOffering(
            @PathVariable UUID courseOfferingId,
            @PathVariable UUID studentId) {

        professorServiceImpl.removeStudentFromCourseOffering(courseOfferingId, studentId);
    }
}