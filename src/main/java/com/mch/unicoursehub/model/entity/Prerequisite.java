package com.mch.unicoursehub.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "prerequisites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prerequisite {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // درسی که یک پیش‌نیاز دارد
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // پیش‌نیاز آن درس
    @ManyToOne
    @JoinColumn(name = "prerequisite_id", nullable = false)
    private Course prerequisite;

}