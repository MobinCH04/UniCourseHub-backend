package com.mch.unicoursehub.model.entity;

import com.mch.unicoursehub.model.enums.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID tid;

    @Column(name = "uuid", nullable = false, unique = true)
    UUID uuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false,length = 30)
    TokenType type;

    @CreationTimestamp
    LocalDateTime creationTime;

    @ManyToOne
    @JoinColumn(name = "uid", referencedColumnName = "uid")
    User user;

    boolean revoked;

}
