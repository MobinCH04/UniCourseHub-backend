package com.mch.unicoursehub.model.dto;

import com.mch.unicoursehub.model.enums.DayOfWeek;

import java.util.List;
/**
 * DTO representing a list of time slots grouped by a specific day of the week.
 *
 * <p>
 * Contains the day and a list of time slots scheduled for that day.
 * </p>
 *
 * @param day   the day of the week for which the time slots are provided
 * @param slots the list of time slots for the given day
 */
public record TimeSlotByDayResponseDto(
        DayOfWeek day,
        List<TimeSlotItemDto> slots
) {}
