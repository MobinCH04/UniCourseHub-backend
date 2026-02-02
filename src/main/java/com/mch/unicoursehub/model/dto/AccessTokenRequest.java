package com.mch.unicoursehub.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

/**
 * DTO representing a request to obtain a new access token using a refresh token.
 *
 * <p>
 * This object is used when a client wants to refresh their authentication
 * token without re-authenticating with username/password.
 * </p>
 *
 * @param refreshToken the refresh token used to request a new access token; must not be blank or null
 */
public record AccessTokenRequest(
        @NotBlank
        @NonNull
        String refreshToken
) {
}
