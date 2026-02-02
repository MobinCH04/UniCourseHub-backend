package com.mch.unicoursehub.model.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.List;

/**
 * DTO representing the request to create a new course.
 *
 * <p>
 * Contains all necessary information to create a course, including its code,
 * name, number of units, and optional prerequisite courses.
 * </p>
 */
@Builder
public record CreateCourseRequest(

        /**
         * Unique code identifying the course.
         * Must contain only digits and cannot be blank.
         */
        @NotBlank(message = "course code is required")
        @Pattern(regexp = "\\d+", message = "course code must contain only digits")
        String code,

        /**
         * Name of the course. Cannot be blank.
         */
        @NotBlank(message = "course name is required")
        String name,

        /**
         * Number of units for the course.
         * Must be between 1 and 4 inclusive.
         */
        @NotNull
        @Min(value = 1, message = "unit must be >=1")
        @Max(value = 4, message = "unit must be <=4")
        int unit,

        /**
         * Optional list of prerequisite course codes (course.code)
         * If empty or null -> no prerequisites
         */
        List<@Pattern(regexp = "\\d+", message = "each prerequisite code must contain only digits") String> prerequisiteCodes
) {
}
