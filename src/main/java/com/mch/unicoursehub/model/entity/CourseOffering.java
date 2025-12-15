package com.mch.unicoursehub.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "course_offerings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;


    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private User professor;


    @ManyToOne
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;


    @Column(nullable = false)
    private int capacity;


    @Column(name = "exam_date", nullable = false)
    private LocalDateTime examDate;

    private int section;

    private String classRoom;

    @ManyToMany
    @JoinTable(
            name = "course_offering_times",
            joinColumns = @JoinColumn(name = "course_offering_id"),
            inverseJoinColumns = @JoinColumn(name = "time_slot_id")
    )
    private List<TimeSlot> timeSlots = new ArrayList<>();


    @OneToMany(mappedBy = "courseOffering")
    private List<Enrollment> enrollments = new ArrayList<>();
}
