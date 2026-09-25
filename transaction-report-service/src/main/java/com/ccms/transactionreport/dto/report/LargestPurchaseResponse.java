package com.ccms.transactionreport.dto.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LargestPurchaseResponse(
        Long transactionId,
        String cardNumber,
        Long merchantId,
        String merchantName,
        BigDecimal amount,
        LocalDateTime transactionTimestamp
) {
}