package com.ccms.transactionreport.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccms.transactionreport.dto.report.MerchantSalesReportResponse;
import com.ccms.transactionreport.dto.report.MerchantTransactionCountReportResponse;
import com.ccms.transactionreport.entity.CardTransaction;
import com.ccms.transactionreport.enums.TransactionStatus;
import com.ccms.transactionreport.enums.TransactionType;
import com.ccms.transactionreport.repository.CardTransactionRepository;
import com.ccms.transactionreport.repository.MerchantRepository;

@Service
@Transactional(readOnly = true)
public class MerchantAnalysisReportService {

    private final CardTransactionRepository cardTransactionRepository;
    private final MerchantRepository merchantRepository;

    public MerchantAnalysisReportService(
            CardTransactionRepository cardTransactionRepository,
            MerchantRepository merchantRepository) {

        this.cardTransactionRepository = cardTransactionRepository;
        this.merchantRepository = merchantRepository;
    }

    public List<MerchantSalesReportResponse> getMerchantsWithHighestSales() {
        Map<Long, BigDecimal> salesByMerchant = new HashMap<>();

        for (CardTransaction transaction : getSuccessfulPurchases()) {
            if (transaction.getMerchantId() != null) {
                salesByMerchant.merge(
                        transaction.getMerchantId(),
                        transaction.getAmount(),
                        BigDecimal::add
                );
            }
        }

        if (salesByMerchant.isEmpty()) {
            return List.of();
        }

        BigDecimal highestSales = salesByMerchant.values()
                .stream()
                .max(BigDecimal::compareTo)
                .orElseThrow();

        return merchantRepository
                .findAll(Sort.by(Sort.Direction.ASC, "merchantId"))
                .stream()
                .filter(merchant ->
                        salesByMerchant.containsKey(
                                merchant.getMerchantId()
                        ))
                .filter(merchant ->
                        salesByMerchant
                                .get(merchant.getMerchantId())
                                .compareTo(highestSales) == 0
                )
                .map(merchant -> new MerchantSalesReportResponse(
                        merchant.getMerchantId(),
                        merchant.getMerchantName(),
                        salesByMerchant.get(merchant.getMerchantId())
                ))
                .toList();
    }

    public List<MerchantTransactionCountReportResponse>
            getMerchantsWithHighestTransactionCount() {

        Map<Long, Long> transactionCountByMerchant = new HashMap<>();

        for (CardTransaction transaction : getSuccessfulPurchases()) {
            if (transaction.getMerchantId() != null) {
                transactionCountByMerchant.merge(
                        transaction.getMerchantId(),
                        1L,
                        Long::sum
                );
            }
        }

        if (transactionCountByMerchant.isEmpty()) {
            return List.of();
        }

        Long highestTransactionCount = transactionCountByMerchant
                .values()
                .stream()
                .max(Long::compareTo)
                .orElseThrow();

        return merchantRepository
                .findAll(Sort.by(Sort.Direction.ASC, "merchantId"))
                .stream()
                .filter(merchant ->
                        transactionCountByMerchant.containsKey(
                                merchant.getMerchantId()
                        ))
                .filter(merchant ->
                        transactionCountByMerchant
                                .get(merchant.getMerchantId())
                                .equals(highestTransactionCount)
                )
                .map(merchant -> new MerchantTransactionCountReportResponse(
                        merchant.getMerchantId(),
                        merchant.getMerchantName(),
                        transactionCountByMerchant.get(
                                merchant.getMerchantId()
                        )
                ))
                .toList();
    }

    private List<CardTransaction> getSuccessfulPurchases() {
        return cardTransactionRepository.findAll()
                .stream()
                .filter(transaction ->
                        transaction.getTransactionType()
                                == TransactionType.PURCHASE
                )
                .filter(transaction ->
                        transaction.getTransactionStatus()
                                == TransactionStatus.SUCCESS
                )
                .toList();
    }
}