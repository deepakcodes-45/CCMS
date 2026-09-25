package com.ccms.customercard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ccms.customercard.enums.CardStatus;
import com.ccms.customercard.enums.CardType;

public record CardResponse(
        String cardNumber,
        Long customerId,
        CardType cardType,
        BigDecimal creditLimit,
        BigDecimal availableCredit,
        BigDecimal outstandingAmount,
        LocalDate expiryDate,
        CardStatus cardStatus
) {
}