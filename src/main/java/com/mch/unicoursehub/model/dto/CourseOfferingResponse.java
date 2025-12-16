package com.mch.unicoursehub.model.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
