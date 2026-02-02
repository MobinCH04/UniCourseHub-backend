package com.mch.unicoursehub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO representing a request to update a semester's details.
 *
 * <p>
 * Only the provided fields will be updated. Fields left null will remain unchanged.
 * This allows partial updates to a semester's name, start/end dates, and minimum/maximum units.
 * </p>
 *
 * @param name     the new name of the semester; must follow format YYYY-T (e.g., 1404-1); optional
 * @param startDate the new start date of the semester; optional
 * @param endDate   the new end date of the semester; optional
 * @param minUnits  the new minimum number of units a student can enroll in; optional
 * @param maxUnits  the new maximum number of units a student can enroll in; optional
 */
public record UpdateSemesterRequest(

        @Schema(example = "14041")
        @Pattern(
                regexp = "^1[34][0-9]{2}-[123]$",
                message = "Semester name must be in format YYYY-T (e.g. 1404-1)"
        )
        String name,

        @Schema(example = "2025-02-10")
        LocalDate startDate,

        @Schema(example = "2025-07-01")
        LocalDate endDate,

        @Schema(example = "12")
        Integer minUnits,

        @Schema(example = "24")
        Integer maxUnits
) {}
