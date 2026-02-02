package com.mch.unicoursehub.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a specific offering (section) of a course in a semester.
 *
 * <p>
 * Contains details about the course, assigned professor, semester, capacity,
 * scheduled exam date, classroom, section number, time slots, and enrollments.
 * </p>
 */
@Entity
@Table(name = "course_offerings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseOffering {

    /**
     * Unique identifier for the course offering.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    /**
     * The course associated with this offering.
     */
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;


    /**
     * The professor assigned to teach this course offering.
     */
    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private User professor;


    /**
     * The semester in which this course offering takes place.
     */
    @ManyToOne
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;


    /**
     * Maximum number of students allowed to enroll.
     */
    @Column(nullable = false)
    private int capacity;


    /**
     * Scheduled exam date and time for this course offering.
     */
    @Column(name = "exam_date", nullable = false)
    private LocalDateTime examDate;

    /**
     * Section number of this offering.
     */
    private int section;

    /**
     * Classroom assigned for this course offering.
     */
    private String classRoom;

    /**
     * List of time slots assigned to this course offering.
     */
    @ManyToMany
    @JoinTable(
            name = "course_offering_times",
            joinColumns = @JoinColumn(name = "course_offering_id"),
            inverseJoinColumns = @JoinColumn(name = "time_slot_id")
    )
    private List<TimeSlot> timeSlots = new ArrayList<>();


    /**
     * List of student enrollments in this course offering.
     */
    @OneToMany(mappedBy = "courseOffering")
    private List<Enrollment> enrollments = new ArrayList<>();
}
