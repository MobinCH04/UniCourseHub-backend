package com.mch.unicoursehub.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mch.unicoursehub.model.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO representing a request to create a new user.
 *
 * <p>
 * Contains all required information to register a new user, including
 * personal details, contact information, national ID, user number, and role.
 * </p>
 */
public record NewUserRequest(
        /**
         * First name of the user. Must be between 2 and 50 characters and not blank.
         */
        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        /**
         * Last name of the user. Must be between 2 and 50 characters and not blank.
         */
        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        /**
         * User's phone number. Must be a valid Iranian mobile number and not blank.
         */
        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^(\\+98|0)?9\\d{9}$",
                message = "Phone number must be a valid Iranian mobile number"
        )
        String phoneNumber,

        /**
         * National ID of the user. Must be a 10-digit number and not blank.
         */
        @NotBlank
        @NotNull
        @JsonProperty("nationalCode")
        @Pattern(
                regexp = "^\\d{10}$",
                message = "National ID must be a 10-digit number"
        )
        String nationalCode,

        /**
         * Unique user number. Cannot be null or blank.
         */
        @NotNull
        @NotBlank
        String userNumber,

        /**
         * Role assigned to the user (e.g., STUDENT, PROFESSOR, ADMIN). Cannot be null.
         */
        @NotNull(message = "Role is required")
        @JsonProperty("role")
        Role role
) {
}
