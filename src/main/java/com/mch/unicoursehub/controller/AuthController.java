package com.mch.unicoursehub.controller;

import com.mch.unicoursehub.model.dto.AccessTokenRequest;
import com.mch.unicoursehub.model.dto.AuthRequestResponse;
import com.mch.unicoursehub.model.dto.UserLogin;
import com.mch.unicoursehub.service.impl.LoginServiceImpl;
import com.mch.unicoursehub.service.impl.RateLimitServiceImpl;
import com.mch.unicoursehub.service.impl.TokenServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller.
 *
 * <p>
 * This controller handles authentication-related operations such as
 * user login using credentials and refreshing access tokens using
 * a refresh token.
 * </p>
 */
@Tag(name = "Authentication controller")
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    /**
     * Service responsible for applying rate limits on authentication requests.
     */
    private final RateLimitServiceImpl rateLimitServiceImpl;
    /**
     * Service responsible for handling user login logic.
     */
    private final LoginServiceImpl loginServiceImpl;
    /**
     * Service responsible for token generation and validation.
     */
    private final TokenServiceImpl tokenServiceImpl;

    /**
     * Authenticates a user using user number and password.
     *
     * <p>
     * This endpoint is public and applies rate limiting before
     * processing the login request.
     * </p>
     *
     * @param login user login credentials
     * @return authentication response containing tokens and user details
     */
    @Operation(
            summary = "User login by user-number and password",
            description = "This route is public."
    )
    @PostMapping()
    public ResponseEntity<AuthRequestResponse> auth(@Valid @RequestBody UserLogin login){

        // Apply rate limiting to prevent brute-force login attempts
        rateLimitServiceImpl.applyAuthRateLimit(login.userNumber());

        // Authenticate user using credentials
        AuthRequestResponse authRequestResponse = loginServiceImpl.pwdUserLogin(login);

        return ResponseEntity.ok(authRequestResponse);
    }

    /**
     * Generates a new access token using a refresh token.
     *
     * <p>
     * This endpoint is public and allows clients to obtain a new
     * access token without re-authenticating with credentials.
     * </p>
     *
     * @param accessTokenRequest request containing refresh token
     * @return authentication response with a new access token
     */
    @Operation(
            summary = "User login by refresh token",
            description = "This route is public."
    )
    @PostMapping("/refresh")
    public ResponseEntity<AuthRequestResponse> refresh(@Valid @RequestBody AccessTokenRequest accessTokenRequest){

        // Generate a new access token using the provided refresh token
        AuthRequestResponse response  = tokenServiceImpl.newAccessTokenByRefreshToken(accessTokenRequest.refreshToken());

        return ResponseEntity.ok(response);
    }
}
