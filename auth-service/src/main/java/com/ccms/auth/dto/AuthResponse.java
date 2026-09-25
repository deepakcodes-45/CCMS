package com.ccms.auth.dto;

import com.ccms.auth.enums.RoleName;

public record AuthResponse(
        Long userId,
        String username,
        RoleName role,
        Long customerId,
        String token
) {
}