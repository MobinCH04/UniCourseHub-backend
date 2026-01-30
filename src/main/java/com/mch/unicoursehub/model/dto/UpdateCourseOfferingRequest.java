package com.mch.unicoursehub.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UpdateCourseOfferingRequest(
        String professorUserNumber,
        Integer capacity,
        LocalDateTime examDate,
        String classroomNumber,
        List<UUID> timeSlotIds
) {
}
