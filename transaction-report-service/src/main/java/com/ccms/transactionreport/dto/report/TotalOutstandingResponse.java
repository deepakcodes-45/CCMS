package com.ccms.transactionreport.dto.report;

import java.math.BigDecimal;

public record TotalOutstandingResponse(
        BigDecimal totalOutstandingAmount
) {
}