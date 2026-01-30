package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.model.dto.TimeSlotByDayResponseDto;
import com.mch.unicoursehub.model.dto.TimeSlotItemDto;
import com.mch.unicoursehub.model.entity.TimeSlot;
import com.mch.unicoursehub.model.enums.DayOfWeek;
import com.mch.unicoursehub.repository.TimeSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TimeSlotServiceImplTest {

    private TimeSlotRepository timeSlotRepository;
    private TimeSlotServiceImpl service;

    @BeforeEach
    void setUp() {
        timeSlotRepository = mock(TimeSlotRepository.class);
        service = new TimeSlotServiceImpl(timeSlotRepository);
    }

    @Test
    void getAllGroupedByDay_shouldGroupAndSortCorrectly() {
        // آماده‌سازی داده‌ها
        TimeSlot ts1 = TimeSlot.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(10, 0))
                .build();

        TimeSlot ts2 = TimeSlot.builder()
                .dayOfWeek(DayOfWeek.WEDNESDAY)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(16, 0))
                .build();

        TimeSlot ts3 = TimeSlot.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .build();

        when(timeSlotRepository.findAll()).thenReturn(List.of(ts1, ts2, ts3));

        // اجرای سرویس
        List<TimeSlotByDayResponseDto> result = service.getAllGroupedByDay();

        // بررسی نتیجه
        assertThat(result).hasSize(2); // دو روز با تایم اسلات
        assertThat(result.get(0).day()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(result.get(0).slots()).hasSize(2);
        assertThat(result.get(1).day()).isEqualTo(DayOfWeek.WEDNESDAY);
        assertThat(result.get(1).slots()).hasSize(1);

        // بررسی ترتیب تایم اسلات‌ها در هر روز
        assertThat(result.get(0).slots().get(0).startTime()).isEqualTo(ts1.getStartTime());
        assertThat(result.get(0).slots().get(1).startTime()).isEqualTo(ts3.getStartTime());
    }

    @Test
    void getAllGroupedByDay_whenNoTimeSlots_shouldReturnEmptyList() {
        when(timeSlotRepository.findAll()).thenReturn(List.of());

        List<TimeSlotByDayResponseDto> result = service.getAllGroupedByDay();

        assertThat(result).isEmpty();
    }
}
