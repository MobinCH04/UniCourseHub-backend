package com.mch.unicoursehub.service;

import com.mch.unicoursehub.model.dto.TimeSlotByDayResponseDto;

import java.util.List;

/**
 * Service interface for managing time slots.
 * <p>
 * Provides operations for retrieving time slots, typically grouped by day of the week.
 * Useful for scheduling courses and avoiding time conflicts.
 * </p>
 */
public interface TimeSlotService {

    /**
     * Retrieve all time slots grouped by day of the week.
     *
     * @return A list of TimeSlotByDayResponseDto, each containing the day and its time slots.
     */
    List<TimeSlotByDayResponseDto> getAllGroupedByDay();

}
