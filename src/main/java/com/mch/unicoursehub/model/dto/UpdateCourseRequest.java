package com.mch.unicoursehub.model.dto;

import jakarta.validation.constraints.Pattern;

import java.util.List;

/**
 * DTO representing a request to update a course's details.
 *
 * <p>
 * Only the provided fields will be updated. Fields left null will remain unchanged.
 * This allows partial updates to a course's name, unit, and prerequisites.
 * </p>
 *
 * @param name              the new name of the course; optional
 * @param unit              the new number of units for the course; optional
 * @param prerequisiteCodes list of prerequisite course codes; each must contain only digits; optional
 */
public record UpdateCourseRequest(
        String name,
        Integer unit,
        List<@Pattern(regexp = "\\d+", message = "each prerequisite code must contain only digits") String> prerequisiteCodes
) {
}
