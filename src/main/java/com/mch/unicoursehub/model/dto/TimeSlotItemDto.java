package com.mch.unicoursehub.model.dto;

import com.mch.unicoursehub.model.entity.TimeSlot;

import java.time.LocalTime;
import java.util.UUID;

public record TimeSlotItemDto(
        UUID id,
        LocalTime startTime,
        LocalTime endTime
) {
    public static TimeSlotItemDto from(TimeSlot ts) {
        return new TimeSlotItemDto(
                ts.getId(),
                ts.getStartTime(),
                ts.getEndTime()
        );
    }
}
