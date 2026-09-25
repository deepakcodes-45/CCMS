package com.ccms.transactionreport.dto.report;

public record MerchantReportResponse(
        Long merchantId,
        String merchantName,
        String category,
        String location
) {
}