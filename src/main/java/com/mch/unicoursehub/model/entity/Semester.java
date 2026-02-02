package com.mch.unicoursehub.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing an academic semester.
 *
 * <p>
 * Contains information about the semester, including its name, start and end dates,
 * minimum and maximum allowed units, and course offerings within the semester.
 * </p>
 */
@Entity
@Table(name = "semesters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Semester {

    /**
     * Unique identifier for the semester.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    /**
     * Name of the semester (e.g., "1404-1"). Must be unique and not null.
     */
    @Column(length = 20, nullable = false, unique = true)
    private String name;


    /**
     * Start date of the semester.
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;


    /**
     * End date of the semester.
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;


    /**
     * Minimum number of units a student can enroll in during this semester.
     */
    @Column(name = "min_units", nullable = false)
    private int minUnits;


    /**
     * Maximum number of units a student can enroll in during this semester.
     */
    @Column(name = "max_units", nullable = false)
    private int maxUnits;


    /**
     * List of course offerings associated with this semester.
     */
    @OneToMany(mappedBy = "semester")
    private List<CourseOffering> offerings = new ArrayList<>();
}
