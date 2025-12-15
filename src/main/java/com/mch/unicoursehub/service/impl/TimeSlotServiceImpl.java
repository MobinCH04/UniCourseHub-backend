package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.model.dto.TimeSlotByDayResponseDto;
import com.mch.unicoursehub.model.dto.TimeSlotItemDto;
import com.mch.unicoursehub.model.entity.TimeSlot;
import com.mch.unicoursehub.model.enums.DayOfWeek;
import com.mch.unicoursehub.repository.TimeSlotRepository;
import com.mch.unicoursehub.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;

    public List<TimeSlotByDayResponseDto> getAllGroupedByDay() {

        // گروه‌بندی بر اساس روز
        var grouped = timeSlotRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(TimeSlot::getDayOfWeek));

        // مرتب‌سازی روزها بر اساس ترتیب Enum
        return Arrays.stream(DayOfWeek.values())
                .filter(grouped::containsKey) // فقط روزهایی که رکورد دارند
                .map(day -> new TimeSlotByDayResponseDto(
                        day,
                        grouped.get(day).stream()
                                .map(TimeSlotItemDto::from)
                                .toList()
                ))
                .toList();
    }
}

