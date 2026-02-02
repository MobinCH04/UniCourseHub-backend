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

/**
 * Entity representing an authentication or refresh token for a user.
 *
 * <p>
 * Contains information about the token UUID, type, associated user, creation timestamp,
 * and whether the token has been revoked.
 * </p>
 */
@Entity
@Table(name = "tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {

    /**
     * Unique identifier for the token entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID tid;

    /**
     * UUID of the token used for authentication/authorization.
     */
    @Column(name = "uuid", nullable = false, unique = true)
    UUID uuid;

    /**
     * Type of the token (e.g., ACCESS, REFRESH).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false,length = 30)
    TokenType type;

    /**
     * Timestamp indicating when the token was created.
     * Automatically populated on creation.
     */
    @CreationTimestamp
    LocalDateTime creationTime;

    /**
     * The user associated with this token.
     */
    @ManyToOne
    @JoinColumn(name = "uid", referencedColumnName = "uid")
    User user;

    /**
     * Indicates whether the token has been revoked.
     */
    @Column(nullable = false)
    boolean revoked;

}
