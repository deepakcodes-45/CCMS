package com.ccms.transactionreport.security;

public record JwtUserPrincipal(
        Long userId,
        String username,
        RoleName role,
        Long customerId
) {
}