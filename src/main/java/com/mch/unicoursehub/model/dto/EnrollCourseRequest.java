package com.mch.unicoursehub.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EnrollCourseRequest(
        @NotBlank
        String courseCode,

        @NotNull
        Integer groupNumber
) {
}
