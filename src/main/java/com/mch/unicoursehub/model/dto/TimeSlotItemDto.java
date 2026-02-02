package com.mch.unicoursehub.model.dto;

import com.mch.unicoursehub.model.entity.TimeSlot;

import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO representing a single time slot.
 *
 * <p>
 * Contains the unique identifier, start time, and end time of the time slot.
 * </p>
 *
 * @param id        the unique identifier of the time slot
 * @param startTime the start time of the time slot
 * @param endTime   the end time of the time slot
 */
public record TimeSlotItemDto(
        UUID id,
        LocalTime startTime,
        LocalTime endTime
) {
    /**
     * Converts a TimeSlot entity to a TimeSlotItemDto.
     *
     * @param ts the TimeSlot entity to convert
     * @return a new TimeSlotItemDto representing the entity
     */
    public static TimeSlotItemDto from(TimeSlot ts) {
        return new TimeSlotItemDto(
                ts.getId(),
                ts.getStartTime(),
                ts.getEndTime()
        );
    }
}
