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

@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID uid;

    @Column(name = "first_name", nullable = false, length = 50)
    String firstName;

    @Column(name = "last_name" , nullable = false, length = 50)
    String lastName;

    @Column(name = "phone_number", nullable = false, length = 16, unique = true)
    String phoneNumber;

    @Column(name = "password", nullable = false)
    String password;

    @Convert(converter = EncryptionConverter.class)
    @Column(name = "national_code", nullable = false, unique = true, length = 250)
    String nationalCode;

    @Column(name = "user_number", nullable = false, unique = true)
    String userNumber;

    @Column(name = "is_account_locked", nullable = false)
    boolean isAccountLocked;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    Role role;


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
