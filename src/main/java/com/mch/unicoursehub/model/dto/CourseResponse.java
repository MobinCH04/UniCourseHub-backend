package com.mch.unicoursehub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record CourseResponse(
        @Schema(description = "Course related code", example = "777785")
        String code,

        @Schema(description = "Name of the course", example = "مبانی برنامه سازی کامپیوتر")
        String name,

        @Schema(description = "Unit of the course", example = "3")
        int unit,

        @Schema(description = "prerequisites of the course.", example = "777780")
        List<String> prerequisites
) {
}
