package com.mch.unicoursehub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateSemesterRequest(

        @NotBlank
        @Schema(example = "1404-1")
        @Pattern(
                regexp = "^1[34][0-9]{2}-[123]$",
                message = "Semester name must be in format YYYY-T (e.g. 1404-1)"
        )
        String name,

        @NotNull
        @Schema(example = "2025-02-01")
        LocalDate startDate,

        @NotNull
        @Schema(example = "2025-06-30")
        LocalDate endDate,

        @Min(9)
        @Schema(example = "12")
        int minUnits,

        @Max(24)
        @Schema(example = "24")
        int maxUnits
) {}
