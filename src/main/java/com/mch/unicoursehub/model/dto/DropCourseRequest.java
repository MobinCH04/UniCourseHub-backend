package com.mch.unicoursehub.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO representing a request to drop a course for a student.
 *
 * <p>
 * This object contains the necessary information to identify the course
 * offering the student wants to drop, including course code, group number,
 * and the semester name.
 * </p>
 *
 * @param courseCode   the code of the course to be dropped; must not be blank
 * @param groupNumber  the group number of the course offering; must not be null
 * @param semesterName the semester in which the course is offered; must not be blank
 */
public record DropCourseRequest(
        @NotBlank
        String courseCode,

        @NotNull
        Integer groupNumber,

        @NotBlank
        String semesterName
) {
}
