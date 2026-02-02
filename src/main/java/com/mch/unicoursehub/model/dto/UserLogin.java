package com.mch.unicoursehub.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * DTO representing a login request for a user.
 *
 * <p>
 * Contains the necessary credentials for authentication: user number and password.
 * Both fields are mandatory.
 * </p>
 *
 * @param userNumber the unique user number; must not be blank
 * @param password   the user's password; must not be blank
 */
@Builder
public record UserLogin(
        @NotBlank
        String userNumber,

        @NotBlank(message = "Password is mandatory")
        String password
) {
}
