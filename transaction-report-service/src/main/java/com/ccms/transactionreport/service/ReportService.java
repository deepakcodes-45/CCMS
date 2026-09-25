package com.ccms.transactionreport.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccms.transactionreport.dto.report.CardReportResponse;
import com.ccms.transactionreport.dto.report.CustomerReportResponse;
import com.ccms.transactionreport.dto.report.MerchantReportResponse;
import com.ccms.transactionreport.dto.report.TransactionHistoryResponse;
import com.ccms.transactionreport.entity.CardTransaction;
import com.ccms.transactionreport.entity.CreditCard;
import com.ccms.transactionreport.entity.Customer;
import com.ccms.transactionreport.entity.Merchant;
import com.ccms.transactionreport.repository.CardTransactionRepository;
import com.ccms.transactionreport.repository.CreditCardRepository;
import com.ccms.transactionreport.repository.CustomerRepository;
import com.ccms.transactionreport.repository.MerchantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.ccms.transactionreport.security.JwtUserPrincipal;
import com.ccms.transactionreport.security.RoleName;
@Service
@Transactional(readOnly = true)
public class ReportService {

    private final CustomerRepository customerRepository;
    private final CreditCardRepository creditCardRepository;
    private final MerchantRepository merchantRepository;
    private final CardTransactionRepository cardTransactionRepository;

    public ReportService(
            CustomerRepository customerRepository,
            CreditCardRepository creditCardRepository,
            MerchantRepository merchantRepository,
            CardTransactionRepository cardTransactionRepository) {

        this.customerRepository = customerRepository;
        this.creditCardRepository = creditCardRepository;
        this.merchantRepository = merchantRepository;
        this.cardTransactionRepository = cardTransactionRepository;
    }

    public List<CustomerReportResponse> getAllCustomers() {
        return customerRepository
                .findAll(Sort.by(Sort.Direction.ASC, "customerId"))
                .stream()
                .map(this::toCustomerResponse)
                .toList();
    }

    public List<CardReportResponse> getAllCards() {
        Map<Long, String> customerNames = customerRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Customer::getCustomerId,
                        Customer::getCustomerName
                ));

        return creditCardRepository
                .findAll(Sort.by(Sort.Direction.ASC, "cardNumber"))
                .stream()
                .map(card -> toCardResponse(
                        card,
                        customerNames.get(card.getCustomerId())
                ))
                .toList();
    }

    public List<MerchantReportResponse> getAllMerchants() {
        return merchantRepository
                .findAll(Sort.by(Sort.Direction.ASC, "merchantId"))
                .stream()
                .map(this::toMerchantResponse)
                .toList();
    }

    public List<TransactionHistoryResponse> getCompleteTransactionHistory() {
        Map<Long, String> merchantNames = merchantRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Merchant::getMerchantId,
                        Merchant::getMerchantName
                ));

        return cardTransactionRepository
                .findAll(Sort.by(
                        Sort.Direction.DESC,
                        "transactionTimestamp"
                ))
                .stream()
                .map(transaction -> toTransactionHistoryResponse(
                        transaction,
                        merchantNames.get(transaction.getMerchantId())
                ))
                .toList();
    }
    
    public List<TransactionHistoryResponse> getMyTransactionHistory(
            JwtUserPrincipal currentUser) {

        Long customerId = getCustomerIdFromPrincipal(currentUser);

        Map<Long, String> merchantNames = merchantRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Merchant::getMerchantId,
                        Merchant::getMerchantName
                ));

        return cardTransactionRepository
                .findAllByCustomerIdOrderByTimestampDesc(customerId)
                .stream()
                .map(transaction -> toTransactionHistoryResponse(
                        transaction,
                        merchantNames.get(transaction.getMerchantId())
                ))
                .toList();
    }

    private Long getCustomerIdFromPrincipal(
            JwtUserPrincipal currentUser) {

        if (currentUser == null
                || currentUser.role() != RoleName.CUSTOMER
                || currentUser.customerId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This endpoint is available only to customer users"
            );
        }

        return currentUser.customerId();
    }
    private CustomerReportResponse toCustomerResponse(Customer customer) {
        return new CustomerReportResponse(
                customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getEmail(),
                customer.getMobileNumber()
        );
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

    private MerchantReportResponse toMerchantResponse(Merchant merchant) {
        return new MerchantReportResponse(
                merchant.getMerchantId(),
                merchant.getMerchantName(),
                merchant.getCategory(),
                merchant.getLocation()
        );
    }

    private TransactionHistoryResponse toTransactionHistoryResponse(
            CardTransaction transaction,
            String merchantName) {

        return new TransactionHistoryResponse(
                transaction.getTransactionId(),
                transaction.getCardNumber(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getMerchantId(),
                merchantName,
                transaction.getTransactionTimestamp(),
                transaction.getTransactionStatus(),
                transaction.getFailureReason()
        );
    }
}