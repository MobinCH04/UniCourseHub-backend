package com.mch.unicoursehub.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a university course.
 *
 * <p>
 * Contains information about the course, including its unique code, name, unit count,
 * prerequisites, dependent courses, and course offerings.
 * </p>
 */
@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    /**
     * Unique identifier of the course entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID cid;

    /**
     * Course code (unique and not null).
     * Example: "777785"
     */
    @Column(name = "code", nullable = false, unique = true)
    String code;

    /**
     * Name of the course. Cannot be null.
     */
    @Column(name = "name",nullable = false)
    String name;

    /**
     * Number of units of the course.
     */
    @Column(name = "unit")
    int unit;

    /**
     * List of courses for which this course is a prerequisite.
     * This represents the dependent courses that rely on this course.
     */
    @OneToMany(mappedBy = "prerequisite")
    private List<Prerequisite> dependentCourses = new ArrayList<>();

    /**
     * List of prerequisites that this course requires.
     */
    @OneToMany(mappedBy = "course")
    private List<Prerequisite> prerequisites = new ArrayList<>();

    /**
     * List of offerings (sections) of this course.
     */
    @OneToMany(mappedBy = "course")
    private List<CourseOffering> offerings = new ArrayList<>();
}
