package com.mch.unicoursehub.model.dto;

import lombok.Builder;

/**
 * DTO representing the response returned after a successful authentication request.
 *
 * <p>
 * Contains the authenticated user's details along with the generated
 * access and refresh tokens.
 * </p>
 *
 * @param name         the full name of the authenticated user
 * @param role         the role of the authenticated user (e.g., ADMIN, STUDENT, PROFESSOR)
 * @param accessToken  the JWT access token for authentication
 * @param refreshToken the JWT refresh token used to obtain new access tokens
 */
@Builder
public record AuthRequestResponse(
        String name,
        String role,
        String accessToken,
        String refreshToken
) {
}
