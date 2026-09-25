package com.ccms.transactionreport.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccms.transactionreport.dto.report.AveragePurchaseResponse;
import com.ccms.transactionreport.dto.report.DailyTotalResponse;
import com.ccms.transactionreport.dto.report.LargestPurchaseResponse;
import com.ccms.transactionreport.entity.Merchant;
import com.ccms.transactionreport.repository.CardTransactionRepository;
import com.ccms.transactionreport.repository.MerchantRepository;

@Service
@Transactional(readOnly = true)
public class TransactionMetricsReportService {

    private final CardTransactionRepository cardTransactionRepository;
    private final MerchantRepository merchantRepository;

    public TransactionMetricsReportService(
            CardTransactionRepository cardTransactionRepository,
            MerchantRepository merchantRepository) {

        this.cardTransactionRepository = cardTransactionRepository;
        this.merchantRepository = merchantRepository;
    }

    public DailyTotalResponse getTodayPurchaseTotal() {
        return new DailyTotalResponse(
                "PURCHASE",
                valueOrZero(
                        cardTransactionRepository
                                .getTodaySuccessfulPurchaseTotal()
                )
        );
    }

    public DailyTotalResponse getTodayPaymentTotal() {
        return new DailyTotalResponse(
                "PAYMENT",
                valueOrZero(
                        cardTransactionRepository
                                .getTodaySuccessfulPaymentTotal()
                )
        );
    }

    public AveragePurchaseResponse getAveragePurchaseAmount() {
        return new AveragePurchaseResponse(
                valueOrZero(
                        cardTransactionRepository
                                .getAverageSuccessfulPurchaseAmount()
                )
        );
    }

    public List<LargestPurchaseResponse> getLargestPurchases() {
        Map<Long, String> merchantNames = merchantRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Merchant::getMerchantId,
                        Merchant::getMerchantName
                ));

        return cardTransactionRepository
                .findLargestSuccessfulPurchases()
                .stream()
                .map(transaction -> new LargestPurchaseResponse(
                        transaction.getTransactionId(),
                        transaction.getCardNumber(),
                        transaction.getMerchantId(),
                        merchantNames.get(transaction.getMerchantId()),
                        transaction.getAmount(),
                        transaction.getTransactionTimestamp()
                ))
                .toList();
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}