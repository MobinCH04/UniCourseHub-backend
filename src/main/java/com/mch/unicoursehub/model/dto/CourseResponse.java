package com.mch.unicoursehub.model.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CourseResponse(
        String code,

        String name,

        int unit,

        List<String> prerequisites
) {
}
