package com.mch.unicoursehub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * DTO representing the request to create a new semester.
 *
 * <p>
 * Contains the semester name, start and end dates, and constraints
 * on the minimum and maximum number of units a student can enroll in.
 * </p>
 */
public record CreateSemesterRequest(

        /**
         * Name of the semester in the format YYYY-T (e.g., "1404-1").
         * Must match the pattern "^1[34][0-9]{2}-[123]$" and cannot be blank.
         */
        @NotBlank
        @Schema(example = "1404-1")
        @Pattern(
                regexp = "^1[34][0-9]{2}-[123]$",
                message = "Semester name must be in format YYYY-T (e.g. 1404-1)"
        )
        String name,

        /**
         * Start date of the semester. Cannot be null.
         */
        @NotNull
        @Schema(example = "2025-02-01")
        LocalDate startDate,

        /**
         * End date of the semester. Cannot be null.
         */
        @NotNull
        @Schema(example = "2025-06-30")
        LocalDate endDate,

        /**
         * Minimum number of units a student can enroll in this semester.
         * Must be at least 9.
         */
        @Min(9)
        @Schema(example = "12")
        int minUnits,

        /**
         * Maximum number of units a student can enroll in this semester.
         * Must be at most 24.
         */
        @Max(24)
        @Schema(example = "24")
        int maxUnits
) {}
