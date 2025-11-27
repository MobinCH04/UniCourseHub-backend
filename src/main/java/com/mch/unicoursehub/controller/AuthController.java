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

@Tag(name = "Authentication controller")
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final RateLimitServiceImpl rateLimitServiceImpl;
    private final LoginServiceImpl loginServiceImpl;
    private final TokenServiceImpl tokenServiceImpl;

    @Operation(
            summary = "User login by user-number and password",
            description = "This route is public."
    )
    @PostMapping()
    public ResponseEntity<AuthRequestResponse> auth(@Valid @RequestBody UserLogin login){

        rateLimitServiceImpl.applyAuthRateLimit(login.userNumber());

        AuthRequestResponse authRequestResponse = loginServiceImpl.pwdUserLogin(login);

        return ResponseEntity.ok(authRequestResponse);
    }

    @Operation(
            summary = "User login by refresh token",
            description = "This route is public."
    )
    @PostMapping("/refresh")
    public ResponseEntity<AuthRequestResponse> refresh(@Valid @RequestBody AccessTokenRequest accessTokenRequest){

        AuthRequestResponse response  = tokenServiceImpl.newAccessTokenByRefreshToken(accessTokenRequest.refreshToken());

        return ResponseEntity.ok(response);
    }
}
