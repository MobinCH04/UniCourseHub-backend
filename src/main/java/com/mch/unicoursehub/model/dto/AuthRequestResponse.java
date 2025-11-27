package com.mch.unicoursehub.model.dto;

import lombok.Builder;

@Builder
public record AuthRequestResponse(
        String name,
        String role,
        String accessToken,
        String refreshToken
) {
}
