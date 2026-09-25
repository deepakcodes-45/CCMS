package com.ccms.transactionreport.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccms.transactionreport.dto.report.CardUsageReportResponse;
import com.ccms.transactionreport.entity.CardTransaction;
import com.ccms.transactionreport.entity.CreditCard;
import com.ccms.transactionreport.entity.Customer;
import com.ccms.transactionreport.enums.TransactionStatus;
import com.ccms.transactionreport.enums.TransactionType;
import com.ccms.transactionreport.repository.CardTransactionRepository;
import com.ccms.transactionreport.repository.CreditCardRepository;
import com.ccms.transactionreport.repository.CustomerRepository;

@Service
@Transactional(readOnly = true)
public class CardUsageReportService {

    private final CreditCardRepository creditCardRepository;
    private final CustomerRepository customerRepository;
    private final CardTransactionRepository cardTransactionRepository;

    public CardUsageReportService(
            CreditCardRepository creditCardRepository,
            CustomerRepository customerRepository,
            CardTransactionRepository cardTransactionRepository) {

        this.creditCardRepository = creditCardRepository;
        this.customerRepository = customerRepository;
        this.cardTransactionRepository = cardTransactionRepository;
    }

    public List<CardUsageReportResponse> getMostUsedCards() {
        return getCardsByUsage(true);
    }

    public List<CardUsageReportResponse> getLeastUsedCards() {
        return getCardsByUsage(false);
    }

    private List<CardUsageReportResponse> getCardsByUsage(
            boolean mostUsed) {

        List<CreditCard> cards = creditCardRepository
                .findAll(Sort.by(Sort.Direction.ASC, "cardNumber"));

        if (cards.isEmpty()) {
            return List.of();
        }

        Map<String, Long> usageByCard = new HashMap<>();

        for (CreditCard card : cards) {
            usageByCard.put(card.getCardNumber(), 0L);
        }

        for (CardTransaction transaction : getSuccessfulPurchases()) {
            usageByCard.computeIfPresent(
                    transaction.getCardNumber(),
                    (cardNumber, count) -> count + 1
            );
        }

        Long selectedUsageCount;

        if (mostUsed) {
            selectedUsageCount = usageByCard.values()
                    .stream()
                    .max(Long::compareTo)
                    .orElseThrow();
        } else {
            selectedUsageCount = usageByCard.values()
                    .stream()
                    .min(Long::compareTo)
                    .orElseThrow();
        }

        Map<Long, String> customerNames = customerRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Customer::getCustomerId,
                        Customer::getCustomerName
                ));

        return cards.stream()
                .filter(card ->
                        usageByCard.get(card.getCardNumber())
                                .equals(selectedUsageCount)
                )
                .map(card -> new CardUsageReportResponse(
                        card.getCardNumber(),
                        card.getCustomerId(),
                        customerNames.get(card.getCustomerId()),
                        usageByCard.get(card.getCardNumber())
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