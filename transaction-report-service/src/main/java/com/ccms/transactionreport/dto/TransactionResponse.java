package com.ccms.transactionreport.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ccms.transactionreport.enums.TransactionStatus;
import com.ccms.transactionreport.enums.TransactionType;

public record TransactionResponse(
        Long transactionId,
        String cardNumber,
        TransactionType transactionType,
        BigDecimal amount,
        Long merchantId,
        LocalDateTime transactionTimestamp,
        TransactionStatus transactionStatus,
        String failureReason,
        BigDecimal availableCredit,
        BigDecimal outstandingAmount
) {
}