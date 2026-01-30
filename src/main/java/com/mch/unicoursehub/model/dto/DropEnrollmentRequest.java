package com.mch.unicoursehub.model.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DropEnrollmentRequest(
        @Schema(description = "Course code for the offering", example = "777785")
        @NotBlank
        String courseCode,

        @Schema(description = "section number for the offering", example = "1")
        @NotNull
        int groupNumber,

        @Schema(description = "semester name for the offering", example = "1404-1")
        @NotBlank
        String semesterName,

        @Schema(description = "student user number for the enrollment")
        @NotBlank
        String studentUserNumber
) {}
