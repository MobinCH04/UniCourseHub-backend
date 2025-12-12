package com.mch.unicoursehub.model.dto;

import com.mch.unicoursehub.model.enums.Role;
import lombok.Builder;

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
