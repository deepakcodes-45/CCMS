package com.ccms.transactionreport.dto.report;

import java.math.BigDecimal;

public record AveragePurchaseResponse(
        BigDecimal averagePurchaseAmount
) {
}