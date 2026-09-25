package com.ccms.transactionreport.dto.report;

public record CardUsageReportResponse(
        String cardNumber,
        Long customerId,
        String customerName,
        Long successfulPurchaseCount
) {
}