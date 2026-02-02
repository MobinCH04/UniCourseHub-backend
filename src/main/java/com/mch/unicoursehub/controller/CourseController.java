package com.mch.unicoursehub.controller;

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

/**
 * Course management controller.
 *
 * <p>
 * This controller provides administrative endpoints for managing courses,
 * including creating, retrieving, updating, and deleting course records.
 * All routes in this controller are intended for ADMIN access only.
 * </p>
 */
@Tag(name = "Course controller")
@RestController
@RequestMapping("/admin/courses")
@AllArgsConstructor
public class CourseController {

    /**
     * Service responsible for course-related business logic.
     */
    private final CourseServiceImpl courseServiceImpl;

    /**
     * Creates a new course.
     *
     * <p>
     * This endpoint is restricted to ADMIN users and validates
     * the input request before creating the course.
     * </p>
     *
     * @param createCourseRequest request containing course creation data
     * @return the created course details
     */
    @Operation(
            summary = "Create course",
            description = "This route is just for ADMIN."
    )
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse createCourse(@Valid @RequestBody CreateCourseRequest createCourseRequest) {

        return courseServiceImpl.createCourse(createCourseRequest);

    }

    /**
     * Retrieves a paginated list of courses.
     *
     * <p>
     * This endpoint supports pagination and optional filtering
     * by course code, name, and unit.
     * </p>
     *
     * @param page page number (default is 1)
     * @param size page size (default is 8)
     * @param code optional course code filter
     * @param name optional course name filter
     * @param unit optional course unit filter
     * @return paginated list of courses
     */
    @Operation(
            summary = "get courses",
            description = "this route can using by ADMIN."
    )
    @GetMapping()
    public ResponseEntity<Pagination<CourseResponse>> getAllCourses(@RequestParam(defaultValue = "1", required = false, name = "p")
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

        Pagination<CourseResponse> result = courseServiceImpl.getAllCourses(page, size, code, name, unit);
        return ResponseEntity.ok(result);
    }

    /**
     * Updates an existing course.
     *
     * <p>
     * Only the fields provided in the request body will be updated.
     * </p>
     *
     * @param code course code
     * @param request request containing updated course data
     * @return updated course details
     */
    @Operation(summary = "Update course", description = "Update course details. Only provided fields will be changed.")
    @PutMapping("/{code}")
    public CourseResponse updateCourse(
            @PathVariable String code,
            @RequestBody UpdateCourseRequest request) {

        return courseServiceImpl.updateCourse(code, request);
    }

    /**
     * Deletes a course by its code.
     *
     * <p>
     * This endpoint permanently removes the course from the system
     * and is restricted to ADMIN users.
     * </p>
     *
     * @param code course code
     */
    @Operation(summary = "Delete a course", description = "This route is for ADMIN only")
    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCourse(@PathVariable String code) {
        courseServiceImpl.deleteCourse(code);
    }

}
