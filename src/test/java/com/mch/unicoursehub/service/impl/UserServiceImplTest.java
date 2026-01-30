package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.exceptions.BadRequestException;
import com.mch.unicoursehub.exceptions.NotFoundException;
import com.mch.unicoursehub.exceptions.UnAuthorizedException;
import com.mch.unicoursehub.model.dto.EditUserRequest;
import com.mch.unicoursehub.model.dto.NewUserRequest;
import com.mch.unicoursehub.model.entity.User;
import com.mch.unicoursehub.model.enums.Role;
import com.mch.unicoursehub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        service = new UserServiceImpl(userRepository, passwordEncoder);
    }

    // ======= createUser tests =======

    @Test
    void createUser_shouldSaveUser_whenValidRequest() {
        NewUserRequest req = new NewUserRequest(
                "John",                       // firstName
                "Doe",                        // lastName
                "09120000000",                // phoneNumber
                "1234567890",                 // nationalCode
                "U123",                        // userNumber
                Role.STUDENT                   // role
        );

        when(userRepository.findByUserNumber("U123")).thenReturn(Optional.empty());
        when(userRepository.findUserByNationalCode("1234567890")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234567890")).thenReturn("encodedPass");

        service.createUser(req);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(captor.capture());

        User saved = captor.getValue();
        assertThat(saved.getFirstName()).isEqualTo("John");
        assertThat(saved.getLastName()).isEqualTo("Doe");
        assertThat(saved.getPhoneNumber()).isEqualTo("09120000000");
        assertThat(saved.getNationalCode()).isEqualTo("1234567890");
        assertThat(saved.getUserNumber()).isEqualTo("U123");
        assertThat(saved.getRole()).isEqualTo(Role.STUDENT);
        assertThat(saved.getPassword()).isEqualTo("encodedPass");
    }

    @Test
    void createUser_shouldThrow_whenUserNumberExists() {
        NewUserRequest req = new NewUserRequest(
                "John", "Doe", "09120000000", "1234567890", "U123", Role.STUDENT
        );

        when(userRepository.findByUserNumber("U123"))
                .thenReturn(Optional.of(new User()));

        assertThrows(BadRequestException.class, () -> service.createUser(req));
    }

    @Test
    void createUser_shouldThrow_whenNationalCodeExists() {
        NewUserRequest req = new NewUserRequest(
                "John", "Doe", "09120000000", "1234567890", "U123", Role.STUDENT
        );

        when(userRepository.findByUserNumber("U123")).thenReturn(Optional.empty());
        when(userRepository.findUserByNationalCode("1234567890"))
                .thenReturn(Optional.of(new User()));

        assertThrows(BadRequestException.class, () -> service.createUser(req));
    }

    @Test
    void createUser_shouldThrow_whenRoleAdmin() {
        NewUserRequest req = new NewUserRequest(
                "John", "Doe", "09120000000", "1234567890", "U123", Role.ADMIN
        );

        assertThrows(BadRequestException.class, () -> service.createUser(req));
    }

    // ======= getUserLoggedInRef tests =======

    @Test
    void getUserLoggedInRef_shouldReturnUser_whenExists() {
        User user = new User();
        user.setUid(UUID.randomUUID());

        Authentication auth = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);

        when(context.getAuthentication()).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("U123");

        SecurityContextHolder.setContext(context);

        when(userRepository.findByUsernameRef("U123")).thenReturn(Optional.of(user));

        User result = service.getUserLoggedInRef();
        assertThat(result).isEqualTo(user);
    }

    @Test
    void getUserLoggedInRef_shouldThrow_whenNotLoggedIn() {
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(context);

        assertThrows(UnAuthorizedException.class, () -> service.getUserLoggedInRef());
    }

    @Test
    void getUserLoggedInRef_shouldThrow_whenUserNotFound() {
        Authentication auth = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("U123");
        SecurityContextHolder.setContext(context);

        when(userRepository.findByUsernameRef("U123")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getUserLoggedInRef());
    }

    // ======= editUser tests =======

    @Test
    void editUser_shouldUpdateFields() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setUid(id);
        user.setRole(Role.STUDENT);
        user.setUserNumber("U123");
        user.setPassword("encoded");

        User loggedIn = new User();
        loggedIn.setUid(UUID.randomUUID());
        loggedIn.setRole(Role.ADMIN);

        EditUserRequest req = new EditUserRequest(
                "Jane",                 // firstName
                "Doe",                  // lastName
                "0987654321",           // phoneNumber
                "9876543210",           // nationalCode
                "U123",                 // userNumber
                Role.PROFESSOR,         // role
                false,                  // isUserLocked
                "newPass"               // password
        );

        // Spy روی سرویس برای mock کردن متدهای داخلی
        UserServiceImpl spyService = spy(service);
        doReturn(Optional.of(user)).when(spyService).getUserByUserNumber("U123");
        doReturn(loggedIn).when(spyService).getUserLoggedInRef();
        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNew");

        spyService.editUser("U123", req);

        assertThat(user.getFirstName()).isEqualTo("Jane");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getPhoneNumber()).isEqualTo("0987654321");
        assertThat(user.getNationalCode()).isEqualTo("9876543210");
        assertThat(user.getRole()).isEqualTo(Role.PROFESSOR);
        assertThat(user.isAccountLocked()).isFalse();
        assertThat(user.getPassword()).isEqualTo("encodedNew");
    }
}
