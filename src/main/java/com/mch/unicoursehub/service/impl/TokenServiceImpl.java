package com.mch.unicoursehub.service.impl;

import com.mch.unicoursehub.model.dto.AuthRequestResponse;
import com.mch.unicoursehub.model.entity.Token;
import com.mch.unicoursehub.model.entity.User;
import com.mch.unicoursehub.model.enums.TokenType;
import com.mch.unicoursehub.repository.TokenRepository;
import com.mch.unicoursehub.repository.UserRepository;
import com.mch.unicoursehub.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service responsible for managing authentication tokens for users.
 *
 * <p>This service handles the generation, validation, revocation, and lifecycle
 * management of access and refresh tokens. It interacts with {@link TokenRepository}
 * for persistence and {@link JwtService} for JWT token generation and parsing.</p>
 *
 * <p>All write operations are transactional with proper isolation levels
 * to ensure consistency in multi-threaded or concurrent environments.</p>
 *
 * <p>Token types supported include {@link com.mch.unicoursehub.model.enums.TokenType#ACCESS_TOKEN}
 * and {@link com.mch.unicoursehub.model.enums.TokenType#REFRESH_TOKEN}.</p>
 *
 * @see JwtService
 * @see TokenRepository
 * @see User
 */
@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
@Service
@RequiredArgsConstructor
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TokenServiceImpl {

    private final TokenRepository tokenRepository;
    private final TokenServiceImpl service;
    private final JwtService jwtService;
    private final UserRepository userRepository;


    /**
     * Generates a new access token for the given user and stores it in the database.
     *
     * @param user the user for whom the access token is generated
     * @return a JWT access token as a {@link String}
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public String newAccessToken(User user) {

        UUID uuid = UUID.randomUUID();

        String access = jwtService.generateToken(user, uuid);

        // Build a new Token object with the provided parameters
        Token build = Token.builder()
                .user(user)
                .uuid(uuid)
                .type(TokenType.ACCESS_TOKEN)
                .build();

        saveToken(build);

        return access;
    }

    /**
     * Generates a new refresh token for the given user and stores it in the database.
     *
     * @param user the user for whom the refresh token is generated
     * @return a JWT refresh token as a {@link String}
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public String newRefreshToken(User user) {

        UUID uuid = UUID.randomUUID();

        String refresh = jwtService.generateRefreshToken(Map.of("uuid", uuid), user);

        // Build a new Token object with the provided parameters
        Token build = Token.builder()
                .user(user)
                .uuid(uuid)
                .type(TokenType.REFRESH_TOKEN)
                .build();

        saveToken(build);

        return refresh;
    }

    /**
     * Generates a new pair of access and refresh tokens using a valid refresh token.
     *
     * <p>This method validates the existing refresh token, revokes it, and issues
     * new tokens to maintain session continuity and security.</p>
     *
     * @param refreshToken the refresh token used to generate new tokens
     * @return an {@link AuthRequestResponse} containing the new access and refresh tokens
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public AuthRequestResponse newAccessTokenByRefreshToken(String refreshToken) {

        UUID tokenUuid = UUID.fromString(jwtService.extractUUID(refreshToken));
        String username = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByUserNumber(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Token storedRefresh = checkToken(tokenUuid, TokenType.REFRESH_TOKEN)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

       revokeToken(storedRefresh);

       UUID newUuid = UUID.randomUUID();

       String newAccessToken = jwtService.generateToken(user, newUuid);

       Token access = Token.builder()
               .user(user)
               .uuid(newUuid)
               .type(TokenType.ACCESS_TOKEN)
               .build();
       tokenRepository.saveAndFlush(access);

       String newRefreshToken = jwtService.generateRefreshToken(
               Map.of("uuid", newUuid),
               user);

       Token refresh = Token.builder()
               .user(user)
               .uuid(newUuid)
               .type(TokenType.REFRESH_TOKEN)
               .build();
       tokenRepository.saveAndFlush(refresh);

       return new AuthRequestResponse(
               user.fullName(),
               user.getRole().name(),
               newAccessToken,
               newRefreshToken
       );
    }

    /**
     * This method applies the token management policy for a specific user.
     * It checks the user's tokens and, if the number of active sessions exceeds the allowed limit,
     * it revokes the oldest tokens.
     *
     * @param user The user for whom the token management policy is applied.
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public void applyTokenPolicy(User user) {
        // Retrieve the list of tokens associated with the user
        List<Token> tokens = checkToken(user);

        // Sort the tokens based on the creation date in ascending order (oldest to newest)
        tokens = tokens.stream()
                .sorted(Comparator.comparing(Token::getCreationTime))
                .toList();

        // Get the maximum allowed sessions for the user's role
        int maxSession = user.getRole().getMaxSession();

        // If the number of tokens is less than the maximum allowed sessions, no need to revoke any tokens
        if (tokens.size() < maxSession) {
            return;
        }

        // Calculate the number of tokens that need to be revoked
        int toDelete = tokens.size() - maxSession + 1;

        // Revoke the oldest tokens based on the calculated number
        List<UUID> list = tokens.stream()
                .limit(toDelete)
                .map(token -> token.getTid())
                .toList();

        tokenRepository.deleteAllByIds(list);
    }

    /**
     * This method saves a new token to the repository.
     *
     * @param token The token object to be saved.
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public void saveToken(Token token) {
        // Save the token to the database
        tokenRepository.saveAndFlush(token);
    }

    /**
     * Revokes all tokens associated with the given user.
     *
     * @param user the user whose tokens need to be revoked
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public void revokeTokens(User user) {
        List<Token> tokens = checkToken(user);
        if (!tokens.isEmpty()) {
            tokenRepository.deleteAll(tokens);  // حذف تمام توکن‌ها یکجا
            tokenRepository.flush();            // اعمال تغییرات در DB
        }
    }

    /**
     * Revokes a token associated with the given UUID.
     *
     * @param uuid the UUID of the token to be revoked
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public void revokeTokenByUUID(UUID uuid) {
        tokenRepository.deleteByUuid(uuid);
    }

    /**
     * Deletes the given token from the repository.
     *
     * @param token the token to be revoked
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public void revokeToken(Token token) {
        tokenRepository.delete(token);
        tokenRepository.flush();
    }

    /**
     * Checks if a token with a specific UUID and type exists.
     *
     * @param uuid the UUID of the token
     * @param type the type of token
     * @return an {@link Optional} containing the token if found, or empty otherwise
     */
    public Optional<Token> checkToken(UUID uuid, TokenType type) {
        return tokenRepository.findByUuidAndType(uuid, type);
    }

    /**
     * Checks if a token exists for the given user.
     *
     * @param user the user whose token needs to be checked
     * @return List containing the tokens if found, otherwise empty
     */
    public List<Token> checkToken(User user) {
        return tokenRepository.findByUser(user);
    }

}