package com.mch.unicoursehub.model.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO representing the details of a course offering.
 *
 * <p>
 * Used to provide information about a specific course offering, including
 * the assigned professor, capacity, schedule, classroom, exam date,
 * group number, and associated time slots.
 * </p>
 *
 * @param courseCode     the unique code identifying the course
 * @param courseName     the name of the course
 * @param professorName  the full name of the professor teaching this offering
 * @param capacity       the maximum number of students allowed in the offering
 * @param examDate       the date and time of the course exam
 * @param classroomNumber the classroom number where the course takes place
 * @param groupNumber    the group number of this course offering
 * @param timeSlotIds    list of IDs representing the scheduled time slots for this course offering
 */
@Builder
public record CourseOfferingResponse(
        String courseCode,
        String courseName,
        String professorName,
        int capacity,
        LocalDateTime examDate,
        int classroomNumber,
        int groupNumber,
        List<UUID> timeSlotIds
) {
}
