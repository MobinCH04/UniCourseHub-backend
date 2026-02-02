package com.mch.unicoursehub.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO representing a request for a student to enroll in a course offering.
 *
 * <p>
 * This object contains the necessary information to identify the course
 * and the specific group/section the student wants to enroll in.
 * </p>
 *
 * @param courseCode  the code of the course to enroll in; must not be blank
 * @param groupNumber the group/section number of the course offering; must not be null
 */
public record EnrollCourseRequest(
        @NotBlank
        String courseCode,

        @NotNull
        Integer groupNumber
) {
}
