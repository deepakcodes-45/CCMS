package com.ccms.customercard.security;

public record JwtUserPrincipal(
        Long userId,
        String username,
        RoleName role,
        Long customerId
) {
}