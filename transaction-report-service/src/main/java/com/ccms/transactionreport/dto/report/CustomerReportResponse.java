package com.ccms.transactionreport.dto.report;

public record CustomerReportResponse(
        Long customerId,
        String customerName,
        String email,
        String mobileNumber
) {
}