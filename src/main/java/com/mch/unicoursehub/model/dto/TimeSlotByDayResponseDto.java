package com.mch.unicoursehub.model.dto;

import com.mch.unicoursehub.model.enums.DayOfWeek;

import java.util.List;

public record TimeSlotByDayResponseDto(
        DayOfWeek day,
        List<TimeSlotItemDto> slots
) {}
