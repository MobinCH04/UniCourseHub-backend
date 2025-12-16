package com.mch.unicoursehub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.List;

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
