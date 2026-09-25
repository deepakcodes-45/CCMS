package com.ccms.transactionreport.dto.report;

import java.math.BigDecimal;

public record MonthlySpendingReportResponse(
        Long customerId,
        String customerName,
        Integer year,
        Integer month,
        BigDecimal totalSpentAmount
) {
}