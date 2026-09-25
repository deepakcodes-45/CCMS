package com.ccms.transactionreport.dto.report;

import java.math.BigDecimal;

public record DailyTotalResponse(
        String transactionType,
        BigDecimal totalAmount
) {
}