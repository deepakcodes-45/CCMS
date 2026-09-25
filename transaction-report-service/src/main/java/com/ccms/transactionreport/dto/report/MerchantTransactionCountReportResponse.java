package com.ccms.transactionreport.dto.report;

public record MerchantTransactionCountReportResponse(
        Long merchantId,
        String merchantName,
        Long transactionCount
) {
}