package com.ccms.transactionreport.dto.report;

import java.math.BigDecimal;

public record CustomerSpendingReportResponse(
        Long customerId,
        String customerName,
        BigDecimal totalSpentAmount
) {
}