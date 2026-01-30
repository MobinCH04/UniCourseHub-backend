package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.model.dto.AuthRequestResponse;
import com.mch.unicoursehub.model.dto.UserLogin;
import com.mch.unicoursehub.model.entity.User;
import com.mch.unicoursehub.model.enums.Role;
import com.mch.unicoursehub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.Default.class)
class LoginServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenServiceImpl tokenService;

    @InjectMocks
    private LoginServiceImpl loginService;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .userNumber("12345")
                .password("pass")
                .role(Role.ADMIN)
                .firstName("Mohammad")
                .lastName("H.")
                .build();
    }

    // -------------------------------------------------------
    // SUCCESS TEST FOR pwdUserLogin()
    // -------------------------------------------------------
    @Test
    void testPwdUserLogin_success() {

        UserLogin login = new UserLogin("12345", "pass");

        // authentication passes
        when(authenticationManager.authenticate(any()))
                .thenReturn(mock(Authentication.class));

        // user exists
        when(userRepository.findByUserNumber("12345"))
                .thenReturn(Optional.of(user));

        // token generation
        when(tokenService.newAccessToken(user)).thenReturn("ACCESS_TOKEN");
        when(tokenService.newRefreshToken(user)).thenReturn("REFRESH_TOKEN");

        AuthRequestResponse res = loginService.pwdUserLogin(login);

        assertEquals("ADMIN", res.role());
        assertEquals(user.fullName(), res.name());
        assertEquals("ACCESS_TOKEN", res.accessToken());
        assertEquals("REFRESH_TOKEN", res.refreshToken());

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByUserNumber("12345");
        verify(tokenService).newAccessToken(user);
        verify(tokenService).newRefreshToken(user);
    }

    @Test
    void testPwdUserLogin_authenticationFails() {

        UserLogin login = new UserLogin("12345", "wrong");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThrows(BadCredentialsException.class,
                () -> loginService.pwdUserLogin(login));

        verify(authenticationManager).authenticate(any());
        verify(userRepository, never()).findByUserNumber(any());
    }

    @Test
    void testPwdUserLogin_userNotFound() {

        UserLogin login = new UserLogin("12345", "pass");

        // authenticate موفق
        when(authenticationManager.authenticate(any()))
                .thenReturn(null);

        // user پیدا نمی‌شود
        when(userRepository.findByUserNumber("12345"))
                .thenReturn(Optional.empty());

        // assertThrows باید نوع Exception صحیح باشد
        assertThrows(NoSuchElementException.class,
                () -> loginService.pwdUserLogin(login));

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByUserNumber("12345");
    }


    @Test
    void testLoginSuccess() {

        when(tokenService.newAccessToken(user)).thenReturn("A1");
        when(tokenService.newRefreshToken(user)).thenReturn("R1");

        AuthRequestResponse res = loginService.loginSuccess(user);

        assertEquals("ADMIN", res.role());
        assertEquals(user.fullName(), res.name());
        assertEquals("A1", res.accessToken());
        assertEquals("R1", res.refreshToken());

        verify(tokenService).newAccessToken(user);
        verify(tokenService).newRefreshToken(user);
    }

    @Test
    void testLogout_success() {

        when(userRepository.findByUserNumber("12345"))
                .thenReturn(Optional.of(user));

        doNothing().when(tokenService).revokeTokens(user);

        loginService.logout("12345");

        verify(userRepository).findByUserNumber("12345");
        verify(tokenService).revokeTokens(user);
    }

    @Test
    void testLogout_userNotFound() {

        when(userRepository.findByUserNumber("12345"))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> loginService.logout("12345"));

        verify(userRepository).findByUserNumber("12345");
        verify(tokenService, never()).revokeTokens(any());
    }

}