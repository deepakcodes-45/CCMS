package com.ccms.transactionreport.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccms.transactionreport.dto.report.CardReportResponse;
import com.ccms.transactionreport.dto.report.TotalOutstandingResponse;
import com.ccms.transactionreport.entity.CreditCard;
import com.ccms.transactionreport.entity.Customer;
import com.ccms.transactionreport.enums.CardStatus;
import com.ccms.transactionreport.repository.CreditCardRepository;
import com.ccms.transactionreport.repository.CustomerRepository;

@Service
@Transactional(readOnly = true)
public class CardRiskReportService {

    private static final BigDecimal TWENTY_PERCENT =
            new BigDecimal("0.20");

    private final CreditCardRepository creditCardRepository;
    private final CustomerRepository customerRepository;

    public CardRiskReportService(
            CreditCardRepository creditCardRepository,
            CustomerRepository customerRepository) {

        this.creditCardRepository = creditCardRepository;
        this.customerRepository = customerRepository;
    }

    public List<CardReportResponse> getBlockedCards() {
        Map<Long, String> customerNames = getCustomerNames();

        return creditCardRepository
                .findAll(Sort.by(Sort.Direction.ASC, "cardNumber"))
                .stream()
                .filter(card -> card.getCardStatus() == CardStatus.BLOCKED)
                .map(card -> toCardResponse(
                        card,
                        customerNames.get(card.getCustomerId())
                ))
                .toList();
    }

    public List<CardReportResponse> getCardsBelowTwentyPercentAvailableCredit() {
        Map<Long, String> customerNames = getCustomerNames();

        return creditCardRepository
                .findAll(Sort.by(Sort.Direction.ASC, "cardNumber"))
                .stream()
                .filter(this::hasAvailableCreditBelowTwentyPercent)
                .map(card -> toCardResponse(
                        card,
                        customerNames.get(card.getCustomerId())
                ))
                .toList();
    }

    public TotalOutstandingResponse getTotalOutstandingAmount() {
        BigDecimal totalOutstandingAmount = creditCardRepository.findAll()
                .stream()
                .map(CreditCard::getOutstandingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TotalOutstandingResponse(totalOutstandingAmount);
    }

    private boolean hasAvailableCreditBelowTwentyPercent(CreditCard card) {
        BigDecimal twentyPercentOfLimit =
                card.getCreditLimit().multiply(TWENTY_PERCENT);

        return card.getAvailableCredit()
                .compareTo(twentyPercentOfLimit) < 0;
    }

    private Map<Long, String> getCustomerNames() {
        return customerRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Customer::getCustomerId,
                        Customer::getCustomerName
                ));
    }

    private CardReportResponse toCardResponse(
            CreditCard card,
            String customerName) {

        return new CardReportResponse(
                card.getCardNumber(),
                card.getCustomerId(),
                customerName,
                card.getCardType(),
                card.getCreditLimit(),
                card.getAvailableCredit(),
                card.getOutstandingAmount(),
                card.getExpiryDate(),
                card.getCardStatus()
        );
    }
}