package com.mch.unicoursehub.controller;

import com.mch.unicoursehub.model.dto.CreateCourseOfferingRequest;
import com.mch.unicoursehub.model.dto.CourseOfferingResponse;
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
            @RequestParam(name = "professorName", required = false) String professorName,
            @RequestParam(name = "courseCode", required = false) String courseCode,
            @RequestParam(name = "courseName", required = false) String courseName
    ) {
        List<CourseOfferingResponse> offerings = courseOfferingServiceImpl.getCourseOfferings(professorName, courseCode, courseName);
        return ResponseEntity.ok(offerings);
    }
    // toDo -> این رو هندل کن که اول کاربر ترم رو وارد کنه بعد بر اساس اون سکشن ها بهش نمایش داده بشه
}
