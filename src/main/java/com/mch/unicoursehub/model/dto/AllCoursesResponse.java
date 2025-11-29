package com.mch.unicoursehub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record AllCoursesResponse(
        @Schema(description = "Course related code", example = "777785")
        String code,

        @Schema(description = "Name of the course", example = "مبانی برنامه سازی کامپیوتر")
        String name,

        @Schema(description = "Unit of the course", example = "3")
        int unit
) {
}
