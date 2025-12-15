package com.mch.unicoursehub.controller;

import com.mch.unicoursehub.model.dto.CreateSemesterRequest;
import com.mch.unicoursehub.model.dto.SemesterResponse;
import com.mch.unicoursehub.model.dto.UpdateSemesterRequest;
import com.mch.unicoursehub.service.SemesterService;
import com.mch.unicoursehub.service.impl.SemesterServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/semesters")
@RequiredArgsConstructor
@Tag(name = "Semester", description = "Operations for managing semesters")
public class SemesterController {

    private final SemesterServiceImpl semesterService;

    @Operation(summary = "Create a new semester", description = "This route is only for ADMIN.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SemesterResponse createSemester(@Valid @RequestBody CreateSemesterRequest request) {
        return semesterService.createSemester(request);
    }

    @Operation(summary = "Update an existing semester", description = "This route is only for ADMIN.")
    @PutMapping("/{name}")
    public SemesterResponse updateSemester(
            @Parameter(description = "Name of the semester to update", example = "1404-1")
            @PathVariable String name,
            @Valid @RequestBody UpdateSemesterRequest request) {

        return semesterService.updateSemester(name, request);
    }

    @Operation(summary = "Get all semesters", description = "This route is available for all users.")
    @GetMapping
    public ResponseEntity<List<SemesterResponse>> getAllSemesters() {
        List<SemesterResponse> semesters = semesterService.getAllSemesters();
        return ResponseEntity.ok(semesters);
    }
}
