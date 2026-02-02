package com.mch.unicoursehub.service;

import com.mch.unicoursehub.model.dto.CourseResponse;
import com.mch.unicoursehub.model.dto.CreateCourseRequest;
import com.mch.unicoursehub.model.dto.UpdateCourseRequest;
import com.mch.unicoursehub.utils.pagination.Pagination;

/**
 * Service interface for managing courses.
 *
 * <p>This service provides operations to create, retrieve, update, and delete courses.
 * A course represents an academic subject with a code, name, unit, and optional prerequisites.</p>
 */
public interface CourseService {

    /**
     * Creates a new course.
     *
     * <p>Validations typically include:
     * <ul>
     *     <li>Unique course code</li>
     *     <li>Unit within allowed range (e.g., 1–4)</li>
     *     <li>Valid prerequisite codes</li>
     * </ul>
     * </p>
     *
     * @param request the request containing course details
     * @return a {@link CourseResponse} representing the created course
     */
    CourseResponse createCourse(CreateCourseRequest request);

    /**
     * Retrieves paginated list of courses with optional filters.
     *
     * <p>All filter parameters are optional. If multiple filters are provided,
     * results are filtered by all of them (AND logic).</p>
     *
     * @param page the page number (1-based index)
     * @param size the number of items per page
     * @param code optional course code filter
     * @param name optional course name filter
     * @param unit optional unit filter
     * @return a {@link Pagination} object containing {@link CourseResponse} items
     */
    Pagination<CourseResponse> getAllCourses(int page, int size, String code, String name, Integer unit);

    /**
     * Updates an existing course.
     *
     * <p>Only provided fields in the {@code UpdateCourseRequest} will be updated.
     * The course is identified by its unique course code.</p>
     *
     * @param code the unique code of the course to update
     * @param req the update request containing fields to modify
     * @return updated {@link CourseResponse} representing the course
     */
    CourseResponse updateCourse(String code, UpdateCourseRequest req);

    /**
     * Deletes an existing course.
     *
     * <p>The course is identified by its unique code. Deletion should handle
     * any related entities such as prerequisites and course offerings.</p>
     *
     * @param code the unique code of the course to delete
     */
    void deleteCourse(String code);
}
