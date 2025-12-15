package com.mch.unicoursehub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record CreateCourseOfferingRequest(

        @Schema(description = "Course code for the offering", example = "777785")
        @NotBlank
        String courseCode,

        @Schema(description = "User number of the professor responsible for the course", example = "12345")
        String professorUserNumber,

        @Schema(description = "Semester name (e.g., 1404-1) for this course offering", example = "1404-1")
        String semesterName,

        @Schema(description = "Capacity of the course offering", example = "30")
        int capacity,

        @Schema(description = "Exam date for the course offering", example = "2025-06-15T09:00")
        LocalDateTime examDate,

        @Schema(description = "Classroom number for this section", example = "101")
        String classroomNumber,

        @Schema(
                description = "List of time slot ids",
                example = "[\"550e8400-e29b-41d4-a716-446655440000\"]"
        )
        List<UUID> timeSlotIds

) {}

