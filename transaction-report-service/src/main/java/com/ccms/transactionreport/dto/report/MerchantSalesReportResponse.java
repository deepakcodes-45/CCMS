package com.ccms.transactionreport.dto.report;

import java.math.BigDecimal;

public record MerchantSalesReportResponse(
        Long merchantId,
        String merchantName,
        BigDecimal totalSalesAmount
) {
}