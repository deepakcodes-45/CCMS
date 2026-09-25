package com.ccms.transactionreport.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ccms.transactionreport.dto.report.AveragePurchaseResponse;
import com.ccms.transactionreport.dto.report.DailyTotalResponse;
import com.ccms.transactionreport.dto.report.LargestPurchaseResponse;
import com.ccms.transactionreport.service.TransactionMetricsReportService;

@RestController
@RequestMapping("/api/reports")
public class TransactionMetricsReportController {

    private final TransactionMetricsReportService
            transactionMetricsReportService;

    public TransactionMetricsReportController(
            TransactionMetricsReportService transactionMetricsReportService) {

        this.transactionMetricsReportService =
                transactionMetricsReportService;
    }

    @GetMapping("/daily/purchases/total")
    public DailyTotalResponse getTodayPurchaseTotal() {
        return transactionMetricsReportService.getTodayPurchaseTotal();
    }

    @GetMapping("/daily/payments/total")
    public DailyTotalResponse getTodayPaymentTotal() {
        return transactionMetricsReportService.getTodayPaymentTotal();
    }

    @GetMapping("/purchases/average-amount")
    public AveragePurchaseResponse getAveragePurchaseAmount() {
        return transactionMetricsReportService.getAveragePurchaseAmount();
    }

    @GetMapping("/purchases/largest")
    public List<LargestPurchaseResponse> getLargestPurchases() {
        return transactionMetricsReportService.getLargestPurchases();
    }
}