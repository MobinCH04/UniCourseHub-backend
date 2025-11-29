package com.mch.unicoursehub.controller;

import com.mch.unicoursehub.model.dto.AllCoursesResponse;
import com.mch.unicoursehub.model.dto.CourseResponse;
import com.mch.unicoursehub.model.dto.CreateCourseRequest;
import com.mch.unicoursehub.model.dto.UpdateCourseRequest;
import com.mch.unicoursehub.service.impl.CourseServiceImpl;
import com.mch.unicoursehub.utils.pagination.Pagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Course controller")
@RestController
@RequestMapping("/admin/courses")
@AllArgsConstructor
public class CourseController {

    private final CourseServiceImpl courseServiceImpl;

    @Operation(
            summary = "Create course",
            description = "This route is just for ADMIN."
    )
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse createCourse(@Valid @RequestBody CreateCourseRequest createCourseRequest) {

        return courseServiceImpl.createCourse(createCourseRequest);

    }

    @Operation(
            summary = "get courses",
            description = "this route can using by ADMIN."
    )
    @GetMapping()
    public ResponseEntity<Pagination<AllCoursesResponse>> getAllCourses(@RequestParam(defaultValue = "1", required = false, name = "p")
                                                                        @Parameter(name = "p", in = ParameterIn.DEFAULT, allowEmptyValue = true, description = "page number")
                                                                        int page,

                                                                        @RequestParam(defaultValue = "8", required = false, name = "s")
                                                                        @Parameter(name = "s", in = ParameterIn.DEFAULT, allowEmptyValue = true, description = "size of page")
                                                                        int size,

                                                                        @RequestParam(required = false, name = "code")
                                                                        @Parameter(name = "name", in = ParameterIn.QUERY, description = "filter by course code",example = "777785")
                                                                        String code,

                                                                        @RequestParam(required = false, name = "name")
                                                                        @Parameter(name = "name", in = ParameterIn.QUERY, description = "filter by course name", example = "مبانی برنامه سازی کامپیوتر")
                                                                        String name,

                                                                        @RequestParam(required = false, name = "unit")
                                                                        @Parameter(name = "unit", in = ParameterIn.QUERY, description = "filter by course unit", example = "3")
                                                                        Integer unit) {

        Pagination<AllCoursesResponse> result = courseServiceImpl.getAllCourses(page, size, code, name, unit);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update course", description = "Update course details. Only provided fields will be changed.")
    @PutMapping("/{code}")
    public CourseResponse updateCourse(
            @PathVariable String code,
            @RequestBody UpdateCourseRequest request) {

        return courseServiceImpl.updateCourse(code, request);
    }
}
