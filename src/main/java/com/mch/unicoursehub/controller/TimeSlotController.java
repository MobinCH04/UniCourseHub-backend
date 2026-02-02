package com.mch.unicoursehub.controller;

import com.mch.unicoursehub.model.dto.TimeSlotByDayResponseDto;
import com.mch.unicoursehub.service.TimeSlotService;
import com.mch.unicoursehub.service.impl.TimeSlotServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for managing time slots.
 *
 * <p>
 * Provides endpoints to retrieve all time slots, grouped by day.
 * This information can be used for scheduling and timetable display.
 * </p>
 */
@RestController
@RequestMapping("/time-slots")
@RequiredArgsConstructor
public class TimeSlotController {

    /**
     * Service responsible for time slot-related business logic.
     */
    private final TimeSlotServiceImpl timeSlotServiceImpl;

    /**
     * Retrieves all time slots grouped by day.
     *
     * @return list of time slots grouped by day
     */
    @GetMapping
    public List<TimeSlotByDayResponseDto> getTimeSlots() {
        return timeSlotServiceImpl.getAllGroupedByDay();
    }
}
