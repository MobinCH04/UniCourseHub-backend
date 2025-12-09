package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.model.dto.AuthRequestResponse;
import com.mch.unicoursehub.model.entity.Token;
import com.mch.unicoursehub.model.entity.User;
import com.mch.unicoursehub.model.enums.Role;
import com.mch.unicoursehub.model.enums.TokenType;
import com.mch.unicoursehub.repository.TokenRepository;
import com.mch.unicoursehub.repository.UserRepository;
import com.mch.unicoursehub.security.service.JwtService;
import com.mch.unicoursehub.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.Default.class)
class TokenServiceImplTest {

    @Mock
    TokenRepository tokenRepository;

    @Mock
    JwtService jwtService;

    @Mock
    TokenServiceImpl mockTokenService;

    @InjectMocks
    TokenServiceImpl tokenService;

    @Mock
    private UserRepository userRepository;

    private User mockUser;
    private List<Token> mockTokens;

    @BeforeEach
    void setup() {
        mockUser = User.builder()
                .uid(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("09120000000")
                .password("123456")
                .nationalCode("1234567890")
                .userNumber("U1001")
                .isAccountLocked(false)
                .role(Role.ADMIN)
                .build();

        mockTokens = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mockTokens.add(Token.builder()
                    .tid(UUID.randomUUID())
                    .creationTime(LocalDateTime.now().minusDays(i))
                    .type(TokenType.ACCESS_TOKEN)
                    .user(mockUser)
                    .build());
        }

        // Spy tokenService to mock checkToken() method
        tokenService = spy(tokenService);

    }

    @Test
    void testNewAccessToken() {
        // Arrange
        when(jwtService.generateToken(any(User.class), any(UUID.class)))
                .thenReturn("mock-access-token");

        // Mock saveAndFlush
        when(tokenRepository.saveAndFlush(any(Token.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        String accessToken = tokenService.newAccessToken(mockUser);

        // Assert
        assertEquals("mock-access-token", accessToken);
        verify(jwtService, times(1)).generateToken(eq(mockUser), any(UUID.class));
        verify(tokenRepository, times(1)).saveAndFlush(any(Token.class));
    }

    @Test
    void testNewRefreshToken() {
        // Arrange
        when(jwtService.generateRefreshToken(anyMap(), eq(mockUser)))
                .thenReturn("mock-refresh-token");

        // Mock saveAndFlush
        when(tokenRepository.saveAndFlush(any(Token.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        String refreshToken = tokenService.newRefreshToken(mockUser);

        // Assert
        assertEquals("mock-refresh-token", refreshToken);
        verify(jwtService, times(1)).generateRefreshToken(anyMap(), eq(mockUser));
        verify(tokenRepository, times(1)).saveAndFlush(any(Token.class));
    }

    @Test
    void testSaveToken() {
        // Arrange
        Token token = mockTokens.get(0);
        doReturn(token).when(tokenRepository).saveAndFlush(token);

        // Act
        tokenService.saveToken(token);

        // Assert
        verify(tokenRepository, times(1)).saveAndFlush(token);
    }

    @Test
    void testRevokeTokens_whenTokensExist() {
        // Arrange
        doReturn(mockTokens).when(tokenService).checkToken(mockUser);
        doNothing().when(tokenRepository).deleteAll(mockTokens);
        doNothing().when(tokenRepository).flush();

        // Act
        tokenService.revokeTokens(mockUser);

        // Assert
        verify(tokenRepository, times(1)).deleteAll(mockTokens);
        verify(tokenRepository, times(1)).flush();
    }

    @Test
    void testRevokeTokens_whenNoTokens() {
        // Arrange
        doReturn(Collections.emptyList()).when(tokenService).checkToken(mockUser);

        // Act
        tokenService.revokeTokens(mockUser);

        // Assert
        verify(tokenRepository, never()).deleteAll(anyList());
        verify(tokenRepository, never()).flush();
    }

}