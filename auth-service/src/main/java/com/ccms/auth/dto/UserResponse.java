package com.ccms.auth.dto;

import com.ccms.auth.enums.RoleName;

public record UserResponse(
        Long userId,
        String username,
        RoleName role,
        Long customerId,
        Boolean enabled
) {
}