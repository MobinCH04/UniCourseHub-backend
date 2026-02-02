package com.mch.unicoursehub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

/**
 * DTO representing the details of a course.
 *
 * <p>
 * Contains basic information about a course, including its code,
 * name, number of units, and any prerequisite courses.
 * </p>
 *
 * @param code          the unique code identifying the course (e.g., "777785")
 * @param name          the name of the course (e.g., "مبانی برنامه سازی کامپیوتر")
 * @param unit          the number of units of the course (e.g., 3)
 * @param prerequisites a list of course codes that are prerequisites for this course
 */
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
