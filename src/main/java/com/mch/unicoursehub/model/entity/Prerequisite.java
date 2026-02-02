package com.mch.unicoursehub.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entity representing a prerequisite relationship between two courses.
 *
 * <p>
 * Each instance defines that a course has a specific prerequisite course.
 * This helps model the dependency between courses in the system.
 * </p>
 */
@Entity
@Table(name = "prerequisites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prerequisite {

    /**
     * Unique identifier for the prerequisite relationship.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The course that has a prerequisite.
     */
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /**
     * The course that is a prerequisite for the above course.
     */
    @ManyToOne
    @JoinColumn(name = "prerequisite_id", nullable = false)
    private Course prerequisite;

}