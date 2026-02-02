package com.mch.unicoursehub.model.entity;

import com.mch.unicoursehub.model.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entity representing a student's enrollment in a specific course offering.
 *
 * <p>
 * Contains information about the student, the course offering they are enrolled in,
 * and the status of the enrollment.
 * </p>
 */
@Entity
@Table(name = "enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {


    /**
     * Unique identifier for the enrollment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    /**
     * The student who is enrolled in the course offering.
     */
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;


    /**
     * The course offering in which the student is enrolled.
     */
    @ManyToOne
    @JoinColumn(name = "course_offering_id", nullable = false)
    private CourseOffering courseOffering;


    /**
     * The status of the enrollment (e.g., ENROLLED, DROPPED).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;
}