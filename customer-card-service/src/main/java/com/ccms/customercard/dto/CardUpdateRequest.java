package com.ccms.customercard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ccms.customercard.enums.CardType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record CardUpdateRequest(

        @NotNull
        CardType cardType,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal creditLimit,

        @NotNull
        LocalDate expiryDate
) {
}