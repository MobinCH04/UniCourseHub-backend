package com.mch.unicoursehub.controller;

import com.mch.unicoursehub.model.dto.CreateCourseOfferingRequest;
import com.mch.unicoursehub.model.dto.CourseOfferingResponse;
import com.mch.unicoursehub.model.dto.UpdateCourseOfferingRequest;
import com.mch.unicoursehub.service.impl.CourseOfferingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing course offerings.
 *
 * <p>
 * Provides endpoints for creating, retrieving, updating, and deleting
 * course offerings. Most operations are restricted to ADMIN users.
 * </p>
 */
@RestController
@RequestMapping("/course-offerings")
@RequiredArgsConstructor
public class CourseOfferingController {

    /**
     * Service responsible for course offering-related business logic.
     */
    private final CourseOfferingServiceImpl courseOfferingServiceImpl;

    /**
     * Creates a new course offering.
     *
     * <p>
     * This endpoint is intended for ADMIN users only. The request body
     * must contain the details of the course offering to be created.
     * </p>
     *
     * @param req the request containing course offering data
     * @return the created course offering details
     */
    @Operation(summary = "Create a new course offering (admin only)")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CourseOfferingResponse> createCourseOffering(
            @RequestBody CreateCourseOfferingRequest req
    ) {
        return ResponseEntity.ok(courseOfferingServiceImpl.createCourseOffering(req));
    }

    /**
     * Retrieves all course offerings with optional filters.
     *
     * <p>
     * Filters can include semester name (required), professor name,
     * course code, and course name. If no filters are provided except
     * semester name, all offerings in that semester will be returned.
     * </p>
     *
     * @param semesterName  the semester to filter offerings by
     * @param professorName optional professor name to filter by
     * @param courseCode    optional course code to filter by
     * @param courseName    optional course name to filter by
     * @return a list of course offerings matching the filters
     */
    @Operation(summary = "Get all course offerings with optional filters")
    @GetMapping()
    public ResponseEntity<List<CourseOfferingResponse>> getCourseOfferings(
            @RequestParam(name = "semesterName") String semesterName,

            @RequestParam(name = "professorName", required = false) String professorName,
            @RequestParam(name = "courseCode", required = false) String courseCode,
            @RequestParam(name = "courseName", required = false) String courseName
    ) {
        List<CourseOfferingResponse> offerings = courseOfferingServiceImpl.getCourseOfferings(semesterName,professorName, courseCode, courseName);
        return ResponseEntity.ok(offerings);
    }

    /**
     * Updates an existing course offering.
     *
     * <p>
     * This endpoint is for ADMIN users. Only the fields provided
     * in the request body will be updated.
     * </p>
     *
     * @param semesterName the semester of the course offering
     * @param courseCode   the course code
     * @param groupNumber  the group number of the offering
     * @param req          request containing updated course offering data
     * @return the updated course offering details
     */
    @Operation(summary = "Update a course offering (admin only)")
    @PutMapping
    public ResponseEntity<CourseOfferingResponse> updateCourseOffering(
            @RequestParam String semesterName,
            @RequestParam String courseCode,
            @RequestParam int groupNumber,
            @RequestBody UpdateCourseOfferingRequest req
    ) {
        return ResponseEntity.ok(
                courseOfferingServiceImpl.updateCourseOffering(
                        semesterName.trim(),
                        courseCode.trim(),
                        groupNumber,
                        req
                )
        );
    }

    /**
     * Deletes a course offering.
     *
     * <p>
     * This endpoint permanently removes a course offering and
     * is restricted to ADMIN users.
     * </p>
     *
     * @param semesterName the semester of the course offering
     * @param courseCode   the course code
     * @param groupNumber  the group number of the offering
     */
    @Operation(summary = "Delete a course offering (admin only)")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourseOffering(
            @RequestParam String semesterName,
            @RequestParam String courseCode,
            @RequestParam int groupNumber
    ) {
        courseOfferingServiceImpl.deleteCourseOffering(
                semesterName.trim(),
                courseCode.trim(),
                groupNumber
        );
    }

}
