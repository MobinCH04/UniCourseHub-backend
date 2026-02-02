package com.mch.unicoursehub.model.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO representing a student's enrollment in a course offering.
 *
 * <p>
 * Provides details about the enrolled course, including course code, name, units,
 * professor, group number, scheduled time slots, and exam date.
 * </p>
 *
 * @param courseCode    the code of the enrolled course
 * @param courseName    the name of the enrolled course
 * @param unit          the number of units of the course
 * @param professorName the name of the professor teaching the course
 * @param groupNumber   the group/section number of the course offering
 * @param timeSlots     list of formatted time slots (e.g., "SATURDAY 08:00-10:00")
 * @param examDate      the date and time of the course exam
 */
public record StudentEnrollmentResponse(
        String courseCode,
        String courseName,
        int unit,

        String professorName,
        int groupNumber,

        List<String> timeSlots, // مثال: "SATURDAY 08:00-10:00"
        LocalDateTime examDate
) {
}
