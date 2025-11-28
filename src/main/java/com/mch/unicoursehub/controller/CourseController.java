package com.mch.unicoursehub.controller;

import com.mch.unicoursehub.model.dto.CourseResponse;
import com.mch.unicoursehub.model.dto.CreateCourseRequest;
import com.mch.unicoursehub.service.impl.CourseServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Course controller")
@RestController
@RequestMapping("/admin/courses")
@AllArgsConstructor
public class CourseController {

    private final CourseServiceImpl courseServiceImpl;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse createCourse(@Valid @RequestBody CreateCourseRequest createCourseRequest) {

        return courseServiceImpl.createCourse(createCourseRequest);

    }
}
