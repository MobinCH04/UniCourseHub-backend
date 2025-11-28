package com.mch.unicoursehub.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID cid;

    @Column(name = "code", nullable = false, unique = true)
    String code;

    @Column(name = "name",nullable = false)
    String name;

    @Column(name = "unit")
    int unit;

    // درس هایی که این درس پیش نیاز آنهاست.
    @OneToMany(mappedBy = "prerequisite")
    private List<Prerequisite> dependentCourses = new ArrayList<>();

    // پیش نیاز هایی که این درس دارد.
    @OneToMany(mappedBy = "course")
    private List<Prerequisite> prerequisites = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<CourseOffering> offerings = new ArrayList<>();
}
