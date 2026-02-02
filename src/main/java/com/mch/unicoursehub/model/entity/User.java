package com.mch.unicoursehub.model.entity;

import com.mch.unicoursehub.model.dto.UserListResponse;
import com.mch.unicoursehub.model.enums.Role;
import com.mch.unicoursehub.utils.EncryptionConverter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a system user.
 *
 * <p>
 * Implements {@link UserDetails} for Spring Security authentication.
 * Contains personal information, credentials, role, and account status.
 * </p>
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class User implements UserDetails {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID uid;

    /**
     * First name of the user.
     */
    @Column(name = "first_name", nullable = false, length = 50)
    String firstName;

    /**
     * Last name of the user.
     */
    @Column(name = "last_name" , nullable = false, length = 50)
    String lastName;

    /**
     * Phone number of the user. Must be unique.
     */
    @Column(name = "phone_number", nullable = false, length = 16, unique = true)
    String phoneNumber;

    /**
     * Encrypted password of the user.
     */
    @Column(name = "password", nullable = false)
    String password;

    /**
     * National code of the user, stored encrypted in the database.
     */
    @Convert(converter = EncryptionConverter.class)
    @Column(name = "national_code", nullable = false, unique = true, length = 250)
    String nationalCode;

    /**
     * Unique user number for the user.
     */
    @Column(name = "user_number", nullable = false, unique = true)
    String userNumber;

    /**
     * Indicates if the user's account is locked.
     */
    @Column(name = "is_account_locked", nullable = false)
    boolean isAccountLocked;

    /**
     * Role of the user (e.g., ADMIN, STUDENT, PROFESSOR).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    Role role;


    // ===================== UserDetails Methods =====================

    /**
     * Returns authorities granted to the user based on their role.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isAccountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {return true;}

    @Override
    public boolean isEnabled() {return true;}

    // ===================== Custom Methods =====================

    /**
     * Returns the full name of the user.
     *
     * @return full name as "firstName lastName"
     */
    public String fullName() {
        return getFirstName() + " " + getLastName();
    }

    /**
     * Constructor to create a user with a unique ID and user number.
     *
     * @param uid the unique identifier of the user
     * @param userNumber the phone number of the user
     */
    public User(UUID uid, String userNumber) {
        this.uid = uid;
        this.userNumber = userNumber;
    }

    public UserListResponse convertToUserListResponse(){
        return UserListResponse.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .userNumber(userNumber)
                .nationalCode(nationalCode)
                .role(role)
                .build();
    }
}
