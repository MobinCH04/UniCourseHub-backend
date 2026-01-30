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

/**
 * Service implementation for managing time slots.
 *
 * <p>This service provides functionality to retrieve and organize
 * {@link TimeSlot} entities, typically used for scheduling classes
 * or course offerings.</p>
 *
 * <p>The main functionality includes grouping all time slots by their
 * {@link com.mch.unicoursehub.model.enums.DayOfWeek} and converting
 * them into DTOs suitable for API responses.</p>
 *
 * @see TimeSlotRepository
 * @see TimeSlotService
 * @see TimeSlotByDayResponseDto
 * @see TimeSlotItemDto
 */
@Service
@RequiredArgsConstructor
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;

    /**
     * Retrieves all time slots from the repository, groups them by
     * {@link com.mch.unicoursehub.model.enums.DayOfWeek}, and converts
     * them into {@link TimeSlotByDayResponseDto} objects.
     *
     * <p>Days with no time slots are excluded, and the resulting list
     * is ordered according to the natural order of {@link DayOfWeek} enum.</p>
     *
     * @return a list of {@link TimeSlotByDayResponseDto}, each representing
     *         a day of the week and its associated time slots
     */
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

