package com.mch.unicoursehub.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO representing a request to update a course offering.
 *
 * <p>
 * Only the provided fields will be updated. Fields left null will remain unchanged.
 * This allows partial updates of a course offering's details such as professor, capacity,
 * exam date, classroom, and time slots.
 * </p>
 *
 * @param professorUserNumber the user number of the professor responsible for the course; optional
 * @param capacity            the capacity of the course offering; optional
 * @param examDate            the date and time of the course exam; optional
 * @param classroomNumber     the classroom number for this course offering; optional
 * @param timeSlotIds         list of time slot IDs for this course offering; optional
 */
public record UpdateCourseOfferingRequest(
        String professorUserNumber,
        Integer capacity,
        LocalDateTime examDate,
        String classroomNumber,
        List<UUID> timeSlotIds
) {
}
