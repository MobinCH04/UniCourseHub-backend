package com.mch.unicoursehub.model.dto;

import java.time.LocalDateTime;
import java.util.List;

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
