package com.ccms.transactionreport.dto.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ccms.transactionreport.enums.TransactionStatus;
import com.ccms.transactionreport.enums.TransactionType;

public record TransactionHistoryResponse(
        Long transactionId,
        String cardNumber,
        TransactionType transactionType,
        BigDecimal amount,
        Long merchantId,
        String merchantName,
        LocalDateTime transactionTimestamp,
        TransactionStatus transactionStatus,
        String failureReason
) {
}