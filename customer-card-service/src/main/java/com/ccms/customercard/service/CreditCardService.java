package com.ccms.customercard.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccms.customercard.dto.CardIssueRequest;
import com.ccms.customercard.dto.CardResponse;
import com.ccms.customercard.dto.CardStatusUpdateRequest;
import com.ccms.customercard.dto.CardUpdateRequest;
import com.ccms.customercard.entity.CreditCard;
import com.ccms.customercard.entity.Customer;
import com.ccms.customercard.enums.CardStatus;
import com.ccms.customercard.exception.BusinessRuleException;
import com.ccms.customercard.exception.DuplicateResourceException;
import com.ccms.customercard.exception.ResourceNotFoundException;
import com.ccms.customercard.repository.CreditCardRepository;
import com.ccms.customercard.repository.CustomerRepository;

@Service
@Transactional
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final CustomerRepository customerRepository;

    public CreditCardService(
            CreditCardRepository creditCardRepository,
            CustomerRepository customerRepository) {

        this.creditCardRepository = creditCardRepository;
        this.customerRepository = customerRepository;
    }

    public CardResponse issueCard(CardIssueRequest request) {
        if (creditCardRepository.existsById(request.cardNumber())) {
            throw new DuplicateResourceException("Card number already exists");
        }

        Customer customer = findCustomer(request.customerId());

        CreditCard card = new CreditCard();
        card.setCardNumber(request.cardNumber());
        card.setCustomer(customer);
        card.setCardType(request.cardType());
        card.setCreditLimit(request.creditLimit());
        card.setAvailableCredit(request.creditLimit());
        card.setOutstandingAmount(BigDecimal.ZERO);
        card.setExpiryDate(request.expiryDate());
        card.setCardStatus(CardStatus.ACTIVE);

        return toResponse(creditCardRepository.save(card));
    }

    public CardResponse getCardByNumber(String cardNumber) {
        return toResponse(findCard(cardNumber));
    }

    public List<CardResponse> getAllCards() {
        return creditCardRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CardResponse> getCardsByCustomerId(Long customerId) {
        findCustomer(customerId);

        return creditCardRepository.findByCustomerCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CardResponse updateCard(
            String cardNumber,
            CardUpdateRequest request) {

        CreditCard card = findCard(cardNumber);

        if (request.creditLimit()
                .compareTo(card.getOutstandingAmount()) < 0) {

            throw new BusinessRuleException(
                    "Credit limit cannot be less than outstanding amount"
            );
        }

        card.setCardType(request.cardType());
        card.setCreditLimit(request.creditLimit());
        card.setAvailableCredit(
                request.creditLimit()
                        .subtract(card.getOutstandingAmount())
        );
        card.setExpiryDate(request.expiryDate());

        return toResponse(creditCardRepository.save(card));
    }

    public CardResponse updateCardStatus(
            String cardNumber,
            CardStatusUpdateRequest request) {

        CreditCard card = findCard(cardNumber);
        card.setCardStatus(request.cardStatus());

        return toResponse(creditCardRepository.save(card));
    }

    private Customer findCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + customerId
                ));
    }

    private CreditCard findCard(String cardNumber) {
        return creditCardRepository.findById(cardNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Card not found with number: " + cardNumber
                ));
    }

    private CardResponse toResponse(CreditCard card) {
        return new CardResponse(
                card.getCardNumber(),
                card.getCustomer().getCustomerId(),
                card.getCardType(),
                card.getCreditLimit(),
                card.getAvailableCredit(),
                card.getOutstandingAmount(),
                card.getExpiryDate(),
                card.getCardStatus()
        );
    }
}