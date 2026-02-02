package com.mch.unicoursehub.model.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO representing a request to drop a student from a course offering.
 *
 * <p>
 * This object contains the necessary information to identify the course
 * offering and the student whose enrollment should be removed.
 * </p>
 *
 * @param courseCode        the code of the course offering; must not be blank
 * @param groupNumber       the group/section number of the course offering; must not be null
 * @param studentUserNumber the user number of the student to be removed; must not be blank
 */
public record DropEnrollmentRequest(
        @Schema(description = "Course code for the offering", example = "777785")
        @NotBlank
        String courseCode,

        @Schema(description = "section number for the offering", example = "1")
        @NotNull
        int groupNumber,

        @Schema(description = "student user number for the enrollment")
        @NotBlank
        String studentUserNumber
) {}
