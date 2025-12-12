package com.mch.unicoursehub.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mch.unicoursehub.model.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EditUserRequest(
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        @Pattern(
                regexp = "^(\\+98|0)?9\\d{9}$",
                message = "Phone number must be a valid Iranian mobile number"
        )
        String phoneNumber,

        @Pattern(
                regexp = "^\\d{10}$",
                message = "National ID must be a 10-digit number"
        )
        String nationalCode,

        String userNumber,

        Role role,

        Boolean isUserLocked,

        String password
) {
}
