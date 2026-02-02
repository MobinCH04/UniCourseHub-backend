package com.mch.unicoursehub.model.dto;

import com.mch.unicoursehub.model.enums.Role;
import lombok.Builder;

/**
 * DTO representing a user's basic information for listing purposes.
 *
 * <p>
 * This object is typically used in admin panels or APIs that return a list of users,
 * providing essential information such as name, contact details, user number, and role.
 * </p>
 *
 * @param firstName    the user's first name
 * @param lastName     the user's last name
 * @param phoneNumber  the user's phone number
 * @param nationalCode the user's national ID number
 * @param userNumber   the unique identifier for the user
 * @param role         the role assigned to the user (e.g., STUDENT, PROFESSOR, ADMIN)
 */
@Builder
public record UserListResponse(
        String firstName,
        String lastName,
        String phoneNumber,
        String nationalCode,
        String userNumber,
        Role role
) {
}
