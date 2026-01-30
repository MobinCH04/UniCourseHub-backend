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

@RestController
@RequestMapping("/course-offerings")
@RequiredArgsConstructor
public class CourseOfferingController {

    private final CourseOfferingServiceImpl courseOfferingServiceImpl;

    @Operation(summary = "Create a new course offering (admin only)")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CourseOfferingResponse> createCourseOffering(
            @RequestBody CreateCourseOfferingRequest req
    ) {
        return ResponseEntity.ok(courseOfferingServiceImpl.createCourseOffering(req));
    }

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
