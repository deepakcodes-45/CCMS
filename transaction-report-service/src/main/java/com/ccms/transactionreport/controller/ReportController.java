package com.ccms.transactionreport.controller;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.ccms.transactionreport.security.JwtUserPrincipal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ccms.transactionreport.dto.report.CardReportResponse;
import com.ccms.transactionreport.dto.report.CustomerReportResponse;
import com.ccms.transactionreport.dto.report.MerchantReportResponse;
import com.ccms.transactionreport.dto.report.TransactionHistoryResponse;
import com.ccms.transactionreport.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/master-data/customers")
    public List<CustomerReportResponse> getAllCustomers() {
        return reportService.getAllCustomers();
    }

    @GetMapping("/master-data/cards")
    public List<CardReportResponse> getAllCards() {
        return reportService.getAllCards();
    }

    @GetMapping("/master-data/merchants")
    public List<MerchantReportResponse> getAllMerchants() {
        return reportService.getAllMerchants();
    }

    @GetMapping("/master-data/transactions")
    public List<TransactionHistoryResponse> getCompleteTransactionHistory() {
        return reportService.getCompleteTransactionHistory();
    }
    
    @GetMapping("/me/transaction-history")
    public List<TransactionHistoryResponse> getMyTransactionHistory(
            @AuthenticationPrincipal JwtUserPrincipal currentUser) {

        return reportService.getMyTransactionHistory(currentUser);
    }
}