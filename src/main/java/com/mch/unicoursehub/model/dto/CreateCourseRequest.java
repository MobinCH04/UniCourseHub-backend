package com.mch.unicoursehub.model.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateCourseRequest(

        @NotBlank(message = "course code is required")
        @Pattern(regexp = "\\d+", message = "course code must contain only digits")
        String code,

        @NotBlank(message = "course name is required")
        String name,

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
