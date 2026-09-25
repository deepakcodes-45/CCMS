package com.ccms.transactionreport.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ccms.transactionreport.dto.report.CardReportResponse;
import com.ccms.transactionreport.dto.report.TotalOutstandingResponse;
import com.ccms.transactionreport.service.CardRiskReportService;

@RestController
@RequestMapping("/api/reports")
public class CardRiskReportController {

    private final CardRiskReportService cardRiskReportService;

    public CardRiskReportController(
            CardRiskReportService cardRiskReportService) {

        this.cardRiskReportService = cardRiskReportService;
    }

    @GetMapping("/cards/blocked")
    public List<CardReportResponse> getBlockedCards() {
        return cardRiskReportService.getBlockedCards();
    }

    @GetMapping("/cards/below-20-percent-available-credit")
    public List<CardReportResponse> getCardsBelowTwentyPercentAvailableCredit() {
        return cardRiskReportService
                .getCardsBelowTwentyPercentAvailableCredit();
    }

    @GetMapping("/outstanding/total")
    public TotalOutstandingResponse getTotalOutstandingAmount() {
        return cardRiskReportService.getTotalOutstandingAmount();
    }
}