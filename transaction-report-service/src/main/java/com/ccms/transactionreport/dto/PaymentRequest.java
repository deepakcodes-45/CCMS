package com.ccms.transactionreport.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PaymentRequest(

        @NotBlank(message = "Card number is required")
        @Pattern(
                regexp = "^[0-9]{16}$",
                message = "Card number must contain exactly 16 digits"
        )
        String cardNumber,

        @NotNull(message = "Payment amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Payment amount must be greater than zero"
        )
        BigDecimal amount
) {
}