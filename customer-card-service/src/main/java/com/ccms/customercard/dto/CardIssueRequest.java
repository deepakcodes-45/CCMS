package com.ccms.customercard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ccms.customercard.enums.CardType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CardIssueRequest(

        @NotBlank
        @Pattern(regexp = "^[0-9]{16}$")
        String cardNumber,

        @NotNull
        Long customerId,

        @NotNull
        CardType cardType,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal creditLimit,

        @NotNull
        LocalDate expiryDate
) {
}