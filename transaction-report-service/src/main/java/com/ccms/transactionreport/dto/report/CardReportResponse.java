package com.ccms.transactionreport.dto.report;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ccms.transactionreport.enums.CardStatus;
import com.ccms.transactionreport.enums.CardType;

public record CardReportResponse(
        String cardNumber,
        Long customerId,
        String customerName,
        CardType cardType,
        BigDecimal creditLimit,
        BigDecimal availableCredit,
        BigDecimal outstandingAmount,
        LocalDate expiryDate,
        CardStatus cardStatus
) {
}