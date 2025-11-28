package com.mch.unicoursehub.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateCourseRequest(

        @NotBlank(message = "course code is required")
        String code,

        @NotBlank(message = "course name is required")
        String name,

        @NotNull
        @Min(value = 1, message = "unit must be >=1")
        int unit,

        /**
         * Optional list of prerequisite course codes (course.code)
         * If empty or null -> no prerequisites
         */
        List<String> prerequisiteCodes
) {
}
