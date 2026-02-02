package com.mch.unicoursehub.model.dto;

import com.mch.unicoursehub.model.enums.Role;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO representing a request to edit a user's information.
 *
 * <p>
 * Contains optional fields to update user details such as name, phone number,
 * national ID, role, account lock status, and password. Only provided fields
 * will be updated.
 * </p>
 *
 * @param firstName    the user's first name; must be between 2 and 50 characters if provided
 * @param lastName     the user's last name; must be between 2 and 50 characters if provided
 * @param phoneNumber  the user's phone number; must be a valid Iranian mobile number if provided
 * @param nationalCode the user's national ID; must be a 10-digit number if provided
 * @param userNumber   the unique user number
 * @param role         the user's role (e.g., STUDENT, PROFESSOR, ADMIN)
 * @param isUserLocked whether the user's account is locked
 * @param password     the user's password; if provided, will be updated
 */
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
