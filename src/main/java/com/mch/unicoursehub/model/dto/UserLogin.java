package com.mch.unicoursehub.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserLogin(
        @NotBlank
        String userNumber,

        @NotBlank(message = "Password is mandatory")
        String password
) {
}
