package com.mch.unicoursehub.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID tid;

    @Column(name = "token_value", nullable = false, updatable = false)
    String tokenValue;

    @CreationTimestamp
    LocalDateTime creationTime;

    @ManyToOne
    @JoinColumn(name = "uid", referencedColumnName = "uid")
    User user;

    @Column(nullable = false)
    boolean revoked;

    @Column(nullable = false)
    boolean expired;
}
