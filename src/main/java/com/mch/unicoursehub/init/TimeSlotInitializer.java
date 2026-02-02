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

/**
 * Component responsible for initializing default time slots in the database.
 *
 * <p>
 * This initializer runs after the Spring context is loaded and will insert
 * predefined time slots for each day of the week if no time slots exist
 * in the database. The time slots cover morning and afternoon periods
 * for weekdays from Saturday to Wednesday.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class TimeSlotInitializer {

    /**
     * Repository for storing and retrieving time slots.
     */
    private final TimeSlotRepository timeSlotRepository;

    /**
     * Initializes default time slots after the application context is loaded.
     *
     * <p>
     * If the database already contains time slots, this method does nothing.
     * Otherwise, it creates time slots for each weekday with predefined
     * start and end times.
     * </p>
     */
    @PostConstruct
    public void init() {

        // If there are already time slots, do nothing
        if (timeSlotRepository.count() > 0) return;

        // List of weekdays
        List<DayOfWeek> days = List.of(
                DayOfWeek.SATURDAY,
                DayOfWeek.SUNDAY,
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY
        );

        // Predefined start and end times
        List<LocalTime[]> times = List.of(
                new LocalTime[]{LocalTime.of(8,0), LocalTime.of(10,0)},
                new LocalTime[]{LocalTime.of(10,0), LocalTime.of(12,0)},
                new LocalTime[]{LocalTime.of(14,0), LocalTime.of(16,0)},
                new LocalTime[]{LocalTime.of(16,0), LocalTime.of(18,0)}
        );

        List<TimeSlot> slots = new ArrayList<>();

        // Create time slots for each day and time
        for (DayOfWeek day : days) {
            for (LocalTime[] t : times) {
                slots.add(TimeSlot.builder()
                        .dayOfWeek(day)
                        .startTime(t[0])
                        .endTime(t[1])
                        .build());
            }
        }

        // Save all time slots to the repository
        timeSlotRepository.saveAll(slots);
    }
}

