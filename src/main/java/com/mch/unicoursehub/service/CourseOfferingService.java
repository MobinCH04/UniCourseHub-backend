package com.mch.unicoursehub.service;

import com.mch.unicoursehub.model.dto.CourseOfferingResponse;
import com.mch.unicoursehub.model.dto.CreateCourseOfferingRequest;
import com.mch.unicoursehub.model.dto.UpdateCourseOfferingRequest;

import java.util.List;

/**
 * Service interface for managing course offerings.
 *
 * <p>This service provides operations to create, retrieve, update, and delete
 * course offerings. A course offering represents a specific section of a course
 * in a particular semester, taught by a professor, with assigned time slots and capacity.</p>
 */
public interface CourseOfferingService {

    /**
     * Creates a new course offering.
     *
     * <p>Validations typically include:
     * <ul>
     *     <li>Course existence</li>
     *     <li>Professor existence</li>
     *     <li>Semester existence</li>
     *     <li>Duplicate offering prevention</li>
     * </ul>
     * </p>
     *
     * @param req the request containing course offering details
     * @return a {@link CourseOfferingResponse} representing the created offering
     */
     CourseOfferingResponse createCourseOffering(CreateCourseOfferingRequest req);

    /**
     * Retrieves course offerings filtered by optional criteria.
     *
     * <p>All filter parameters are optional. If multiple parameters are provided,
     * the results are filtered by all of them (AND logic).</p>
     *
     * @param semesterName optional semester name to filter by
     * @param professorName optional professor full name to filter by
     * @param courseCode optional course code to filter by
     * @param courseName optional course name to filter by
     * @return list of {@link CourseOfferingResponse} matching the filters
     */
     List<CourseOfferingResponse> getCourseOfferings(String semesterName, String professorName, String courseCode, String courseName);

    /**
     * Updates an existing course offering.
     *
     * <p>Only provided fields in the request are updated. The course offering
     * is identified by semester name, course code, and group number.</p>
     *
     * @param semesterName the semester name of the offering
     * @param courseCode the code of the course
     * @param groupNumber the group/section number
     * @param req the update request containing fields to be modified
     * @return updated {@link CourseOfferingResponse} representing the offering
     */
    CourseOfferingResponse updateCourseOffering(
            String semesterName,
            String courseCode,
            int groupNumber,
            UpdateCourseOfferingRequest req
    );

    /**
     * Deletes an existing course offering.
     *
     * <p>The course offering is identified by semester name, course code, and group number.
     * Typically, deletion should handle cascading effects such as removing enrollments.</p>
     *
     * @param semesterName the semester name of the offering
     * @param courseCode the code of the course
     * @param groupNumber the group/section number
     */
    void deleteCourseOffering(String semesterName, String courseCode, int groupNumber);
}
