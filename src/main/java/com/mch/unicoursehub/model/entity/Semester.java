package com.mch.unicoursehub.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "semesters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(length = 20, nullable = false, unique = true)
    private String name;


    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;


    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;


    @Column(name = "min_units", nullable = false)
    private int minUnits;


    @Column(name = "max_units", nullable = false)
    private int maxUnits;


    @OneToMany(mappedBy = "semester")
    private List<CourseOffering> offerings = new ArrayList<>();
}
