package com.ccms.transactionreport.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccms.transactionreport.dto.report.CustomerPaymentReportResponse;
import com.ccms.transactionreport.dto.report.CustomerSpendingReportResponse;
import com.ccms.transactionreport.dto.report.MonthlySpendingReportResponse;
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
public class CustomerAnalysisReportService {

    private final CustomerRepository customerRepository;
    private final CreditCardRepository creditCardRepository;
    private final CardTransactionRepository cardTransactionRepository;

    public CustomerAnalysisReportService(
            CustomerRepository customerRepository,
            CreditCardRepository creditCardRepository,
            CardTransactionRepository cardTransactionRepository) {

        this.customerRepository = customerRepository;
        this.creditCardRepository = creditCardRepository;
        this.cardTransactionRepository = cardTransactionRepository;
    }

    public List<CustomerSpendingReportResponse>
            getCustomersWithHighestSpending() {

        Map<Long, BigDecimal> spendingByCustomer =
                getAmountsByCustomer(TransactionType.PURCHASE);

        if (spendingByCustomer.isEmpty()) {
            return List.of();
        }

        BigDecimal highestSpentAmount = spendingByCustomer.values()
                .stream()
                .max(BigDecimal::compareTo)
                .orElseThrow();

        return getAllCustomers()
                .stream()
                .filter(customer ->
                        spendingByCustomer.containsKey(
                                customer.getCustomerId()
                        ))
                .filter(customer ->
                        spendingByCustomer
                                .get(customer.getCustomerId())
                                .compareTo(highestSpentAmount) == 0
                )
                .map(customer -> new CustomerSpendingReportResponse(
                        customer.getCustomerId(),
                        customer.getCustomerName(),
                        spendingByCustomer.get(customer.getCustomerId())
                ))
                .toList();
    }

    public List<CustomerPaymentReportResponse>
            getCustomersWithHighestPayment() {

        Map<Long, BigDecimal> paymentByCustomer =
                getAmountsByCustomer(TransactionType.PAYMENT);

        if (paymentByCustomer.isEmpty()) {
            return List.of();
        }

        BigDecimal highestPaidAmount = paymentByCustomer.values()
                .stream()
                .max(BigDecimal::compareTo)
                .orElseThrow();

        return getAllCustomers()
                .stream()
                .filter(customer ->
                        paymentByCustomer.containsKey(
                                customer.getCustomerId()
                        ))
                .filter(customer ->
                        paymentByCustomer
                                .get(customer.getCustomerId())
                                .compareTo(highestPaidAmount) == 0
                )
                .map(customer -> new CustomerPaymentReportResponse(
                        customer.getCustomerId(),
                        customer.getCustomerName(),
                        paymentByCustomer.get(customer.getCustomerId())
                ))
                .toList();
    }

    public List<MonthlySpendingReportResponse>
            getMonthlySpendingSummary(int year, int month) {

        List<Customer> customers = getAllCustomers();

        Map<Long, BigDecimal> monthlySpendingByCustomer =
                new HashMap<>();

        for (Customer customer : customers) {
            monthlySpendingByCustomer.put(
                    customer.getCustomerId(),
                    BigDecimal.ZERO
            );
        }

        Map<String, Long> customerIdByCardNumber =
                getCustomerIdByCardNumber();

        for (CardTransaction transaction :
                getSuccessfulTransactionsByType(TransactionType.PURCHASE)) {

            if (transaction.getTransactionTimestamp().getYear() == year
                    && transaction.getTransactionTimestamp()
                            .getMonthValue() == month) {

                Long customerId = customerIdByCardNumber.get(
                        transaction.getCardNumber()
                );

                if (customerId != null) {
                    monthlySpendingByCustomer.merge(
                            customerId,
                            transaction.getAmount(),
                            BigDecimal::add
                    );
                }
            }
        }

        return customers.stream()
                .map(customer -> new MonthlySpendingReportResponse(
                        customer.getCustomerId(),
                        customer.getCustomerName(),
                        year,
                        month,
                        monthlySpendingByCustomer.get(
                                customer.getCustomerId()
                        )
                ))
                .toList();
    }

    private Map<Long, BigDecimal> getAmountsByCustomer(
            TransactionType transactionType) {

        Map<String, Long> customerIdByCardNumber =
                getCustomerIdByCardNumber();

        Map<Long, BigDecimal> amountByCustomer = new HashMap<>();

        for (CardTransaction transaction :
                getSuccessfulTransactionsByType(transactionType)) {

            Long customerId = customerIdByCardNumber.get(
                    transaction.getCardNumber()
            );

            if (customerId != null) {
                amountByCustomer.merge(
                        customerId,
                        transaction.getAmount(),
                        BigDecimal::add
                );
            }
        }

        return amountByCustomer;
    }

    private List<CardTransaction> getSuccessfulTransactionsByType(
            TransactionType transactionType) {

        return cardTransactionRepository.findAll()
                .stream()
                .filter(transaction ->
                        transaction.getTransactionType()
                                == transactionType
                )
                .filter(transaction ->
                        transaction.getTransactionStatus()
                                == TransactionStatus.SUCCESS
                )
                .toList();
    }

    private Map<String, Long> getCustomerIdByCardNumber() {
        return creditCardRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        CreditCard::getCardNumber,
                        CreditCard::getCustomerId
                ));
    }

    private List<Customer> getAllCustomers() {
        return customerRepository
                .findAll(Sort.by(Sort.Direction.ASC, "customerId"));
    }
}