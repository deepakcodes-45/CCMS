package com.ccms.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AdminRegistrationRequest(

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50)
        @Pattern(
                regexp = "^[A-Za-z0-9._-]+$",
                message = "Username may contain letters, numbers, dots, underscores, and hyphens"
        )
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100)
        String password
) {
}