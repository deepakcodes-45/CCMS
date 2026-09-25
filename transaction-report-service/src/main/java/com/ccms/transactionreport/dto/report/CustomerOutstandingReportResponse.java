package com.ccms.transactionreport.dto.report;

import java.math.BigDecimal;

public record CustomerOutstandingReportResponse(
        Long customerId,
        String customerName,
        BigDecimal totalOutstandingAmount
) {
}