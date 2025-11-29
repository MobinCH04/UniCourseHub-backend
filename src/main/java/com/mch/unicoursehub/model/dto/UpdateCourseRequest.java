package com.mch.unicoursehub.model.dto;

import jakarta.validation.constraints.Pattern;

import java.util.List;

public record UpdateCourseRequest(
        String name,
        Integer unit,
        List<@Pattern(regexp = "\\d+", message = "each prerequisite code must contain only digits") String> prerequisiteCodes
) {
}
