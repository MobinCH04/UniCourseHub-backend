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

/**
 * Controller for managing academic semesters.
 *
 * <p>
 * Provides endpoints for creating, updating, and retrieving semesters.
 * Some operations are restricted to ADMIN users, while others
 * are available for all users.
 * </p>
 */
@RestController
@RequestMapping("/semesters")
@RequiredArgsConstructor
@Tag(name = "Semester", description = "Operations for managing semesters")
public class SemesterController {

    /**
     * Service responsible for semester-related business logic.
     */
    private final SemesterServiceImpl semesterService;

    /**
     * Creates a new semester.
     *
     * <p>
     * This endpoint is restricted to ADMIN users. The request body must
     * contain the details of the semester to be created.
     * </p>
     *
     * @param request request containing semester creation data
     * @return the created semester details
     */
    @Operation(summary = "Create a new semester", description = "This route is only for ADMIN.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SemesterResponse createSemester(@Valid @RequestBody CreateSemesterRequest request) {
        return semesterService.createSemester(request);
    }

    /**
     * Updates an existing semester.
     *
     * <p>
     * This endpoint is restricted to ADMIN users. Only the fields provided
     * in the request body will be updated.
     * </p>
     *
     * @param name    the name of the semester to update
     * @param request request containing updated semester data
     * @return the updated semester details
     */
    @Operation(summary = "Update an existing semester", description = "This route is only for ADMIN.")
    @PutMapping("/{name}")
    public SemesterResponse updateSemester(
            @Parameter(description = "Name of the semester to update", example = "1404-1")
            @PathVariable String name,
            @Valid @RequestBody UpdateSemesterRequest request) {

        return semesterService.updateSemester(name, request);
    }

    /**
     * Retrieves all semesters.
     *
     * <p>
     * This endpoint is available for all users.
     * </p>
     *
     * @return list of all semesters
     */
    @Operation(summary = "Get all semesters", description = "This route is available for all users.")
    @GetMapping
    public ResponseEntity<List<SemesterResponse>> getAllSemesters() {
        List<SemesterResponse> semesters = semesterService.getAllSemesters();
        return ResponseEntity.ok(semesters);
    }
}
