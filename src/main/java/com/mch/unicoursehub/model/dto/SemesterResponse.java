package com.mch.unicoursehub.model.dto;

import lombok.Builder;

import java.time.LocalDate;

/**
 * DTO representing the details of a semester.
 *
 * <p>
 * Provides information about a semester, including its name, start and end dates,
 * and the minimum and maximum number of units a student can enroll in.
 * </p>
 *
 * @param name      the name of the semester (e.g., "1404-1")
 * @param startDate the start date of the semester
 * @param endDate   the end date of the semester
 * @param minUnits  the minimum number of units a student can enroll in
 * @param maxUnits  the maximum number of units a student can enroll in
 */
@Builder
public record SemesterResponse(

        String name,
        LocalDate startDate,
        LocalDate endDate,
        int minUnits,
        int maxUnits
) {}

