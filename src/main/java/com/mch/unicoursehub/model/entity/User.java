package com.mch.unicoursehub.model.entity;

import com.mch.unicoursehub.model.enums.Role;
import com.mch.unicoursehub.utils.EncryptionConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @Column(name = "national_code", nullable = false, unique = true, length = 10)
    String nationalCode;

    @Column(name = "user_number", nullable = false, unique = true)
    String userNumber;

    @Column(name = "is_account_locked", nullable = false)
    boolean isAccountLocked;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    Role role;

    @OneToMany(mappedBy = "professor")
    List<CourseOffering> taughtCourses = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    List<Enrollment> enrollments = new ArrayList<>();

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
}
