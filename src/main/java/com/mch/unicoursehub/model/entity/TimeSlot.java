package com.mch.unicoursehub.model.entity;

import com.mch.unicoursehub.model.enums.DayOfWeek;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

/**
 * Entity representing a specific time slot during a day.
 *
 * <p>
 * Each time slot is defined by a day of the week, a start time, and an end time.
 * This entity is used to schedule course offerings and avoid conflicts.
 * </p>
 */
@Entity
@Table(
        name = "time_slots",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"day_of_week", "start_time", "end_time"}
                )
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlot {


    /**
     * Unique identifier for the time slot.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    /**
     * Day of the week for this time slot.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;


    /**
     * Start time of the time slot.
     */
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;


    /**
     * End time of the time slot.
     */
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
}
