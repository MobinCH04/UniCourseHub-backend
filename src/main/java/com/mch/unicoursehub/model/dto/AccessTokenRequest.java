package com.mch.unicoursehub.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record AccessTokenRequest(
        @NotBlank
        @NonNull
        String refreshToken
) {
}
