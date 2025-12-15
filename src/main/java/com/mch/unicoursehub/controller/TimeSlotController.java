package com.mch.unicoursehub.controller;

import com.mch.unicoursehub.model.dto.TimeSlotByDayResponseDto;
import com.mch.unicoursehub.service.TimeSlotService;
import com.mch.unicoursehub.service.impl.TimeSlotServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/time-slots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotServiceImpl timeSlotServiceImpl;

    @GetMapping
    public List<TimeSlotByDayResponseDto> getTimeSlots() {
        return timeSlotServiceImpl.getAllGroupedByDay();
    }
}
