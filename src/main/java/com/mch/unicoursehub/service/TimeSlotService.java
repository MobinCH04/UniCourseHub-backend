package com.mch.unicoursehub.service;

import com.mch.unicoursehub.model.dto.TimeSlotByDayResponseDto;

import java.util.List;

public interface TimeSlotService {

    List<TimeSlotByDayResponseDto> getAllGroupedByDay();

}
