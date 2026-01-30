package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.model.entity.User;
import com.mch.unicoursehub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    private UserRepository userRepository;
    private UserDetailsServiceImpl service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        service = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        // داده نمونه
        User user = new User();
        user.setUid(UUID.randomUUID());
        user.setUserNumber("U12345");

        when(userRepository.findByUserNumber("U12345"))
                .thenReturn(Optional.of(user));

        // اجرای سرویس
        UserDetails result = service.loadUserByUsername("U12345");

        // بررسی نتیجه
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("U12345");

        // بررسی فراخوانی repository
        verify(userRepository, times(1)).findByUserNumber("U12345");
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUserNumber("U12345"))
                .thenReturn(Optional.empty());

        // انتظار پرتاب خطا
        assertThrows(UsernameNotFoundException.class, () ->
                service.loadUserByUsername("U12345")
        );

        verify(userRepository, times(1)).findByUserNumber("U12345");
    }
}
