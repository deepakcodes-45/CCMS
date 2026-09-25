package com.ccms.transactionreport.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ccms.transactionreport.dto.report.CardUsageReportResponse;
import com.ccms.transactionreport.dto.report.MerchantSalesReportResponse;
import com.ccms.transactionreport.dto.report.MerchantTransactionCountReportResponse;
import com.ccms.transactionreport.service.CardUsageReportService;
import com.ccms.transactionreport.service.MerchantAnalysisReportService;

@RestController
@RequestMapping("/api/reports")
public class UsageAnalysisReportController {

    private final MerchantAnalysisReportService
            merchantAnalysisReportService;

    private final CardUsageReportService cardUsageReportService;

    public UsageAnalysisReportController(
            MerchantAnalysisReportService merchantAnalysisReportService,
            CardUsageReportService cardUsageReportService) {

        this.merchantAnalysisReportService =
                merchantAnalysisReportService;

        this.cardUsageReportService = cardUsageReportService;
    }

    @GetMapping("/merchants/highest-sales")
    public List<MerchantSalesReportResponse> getMerchantsWithHighestSales() {
        return merchantAnalysisReportService
                .getMerchantsWithHighestSales();
    }

    @GetMapping("/merchants/highest-transaction-count")
    public List<MerchantTransactionCountReportResponse>
            getMerchantsWithHighestTransactionCount() {

        return merchantAnalysisReportService
                .getMerchantsWithHighestTransactionCount();
    }

    @GetMapping("/cards/most-used")
    public List<CardUsageReportResponse> getMostUsedCards() {
        return cardUsageReportService.getMostUsedCards();
    }

    @GetMapping("/cards/least-used")
    public List<CardUsageReportResponse> getLeastUsedCards() {
        return cardUsageReportService.getLeastUsedCards();
    }
}