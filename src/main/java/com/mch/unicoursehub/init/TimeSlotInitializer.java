package com.mch.unicoursehub.init;

import com.mch.unicoursehub.model.entity.TimeSlot;
import com.mch.unicoursehub.model.enums.DayOfWeek;
import com.mch.unicoursehub.repository.TimeSlotRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TimeSlotInitializer {

    private final TimeSlotRepository timeSlotRepository;

    @PostConstruct
    public void init() {

        if (timeSlotRepository.count() > 0) return;

        List<DayOfWeek> days = List.of(
                DayOfWeek.SATURDAY,
                DayOfWeek.SUNDAY,
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY
        );

        List<LocalTime[]> times = List.of(
                new LocalTime[]{LocalTime.of(8,0), LocalTime.of(10,0)},
                new LocalTime[]{LocalTime.of(10,0), LocalTime.of(12,0)},
                new LocalTime[]{LocalTime.of(14,0), LocalTime.of(16,0)},
                new LocalTime[]{LocalTime.of(16,0), LocalTime.of(18,0)}
        );

        List<TimeSlot> slots = new ArrayList<>();

        for (DayOfWeek day : days) {
            for (LocalTime[] t : times) {
                slots.add(TimeSlot.builder()
                        .dayOfWeek(day)
                        .startTime(t[0])
                        .endTime(t[1])
                        .build());
            }
        }

        timeSlotRepository.saveAll(slots);
    }
}

