package com.ccms.transactionreport.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccms.transactionreport.dto.report.CustomerOutstandingReportResponse;
import com.ccms.transactionreport.dto.report.CustomerPaymentReportResponse;
import com.ccms.transactionreport.dto.report.CustomerSpendingReportResponse;
import com.ccms.transactionreport.dto.report.MonthlySpendingReportResponse;
import com.ccms.transactionreport.service.CustomerAnalysisReportService;
import com.ccms.transactionreport.service.CustomerOutstandingReportService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/reports")
@Validated
public class CustomerAnalysisReportController {

    private final CustomerOutstandingReportService
            customerOutstandingReportService;

    private final CustomerAnalysisReportService
            customerAnalysisReportService;

    public CustomerAnalysisReportController(
            CustomerOutstandingReportService customerOutstandingReportService,
            CustomerAnalysisReportService customerAnalysisReportService) {

        this.customerOutstandingReportService =
                customerOutstandingReportService;

        this.customerAnalysisReportService =
                customerAnalysisReportService;
    }

    @GetMapping("/outstanding/highest")
    public List<CustomerOutstandingReportResponse>
            getCustomersWithHighestOutstanding() {

        return customerOutstandingReportService
                .getCustomersWithHighestOutstanding();
    }

    @GetMapping("/outstanding/lowest")
    public List<CustomerOutstandingReportResponse>
            getCustomersWithLowestOutstanding() {

        return customerOutstandingReportService
                .getCustomersWithLowestOutstanding();
    }

    @GetMapping("/customers/highest-spender")
    public List<CustomerSpendingReportResponse>
            getCustomersWithHighestSpending() {

        return customerAnalysisReportService
                .getCustomersWithHighestSpending();
    }

    @GetMapping("/customers/highest-payer")
    public List<CustomerPaymentReportResponse>
            getCustomersWithHighestPayment() {

        return customerAnalysisReportService
                .getCustomersWithHighestPayment();
    }

    @GetMapping("/customers/monthly-spending")
    public List<MonthlySpendingReportResponse>
            getMonthlySpendingSummary(
                    @RequestParam(name = "year")
                    @Min(2000) Integer year,

                    @RequestParam(name = "month")
                    @Min(1) @Max(12) Integer month) {

        return customerAnalysisReportService
                .getMonthlySpendingSummary(year, month);
    }
}