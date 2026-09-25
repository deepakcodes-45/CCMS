package com.ccms.transactionreport.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccms.transactionreport.dto.report.CustomerOutstandingReportResponse;
import com.ccms.transactionreport.entity.CreditCard;
import com.ccms.transactionreport.entity.Customer;
import com.ccms.transactionreport.repository.CreditCardRepository;
import com.ccms.transactionreport.repository.CustomerRepository;

@Service
@Transactional(readOnly = true)
public class CustomerOutstandingReportService {

    private final CustomerRepository customerRepository;
    private final CreditCardRepository creditCardRepository;

    public CustomerOutstandingReportService(
            CustomerRepository customerRepository,
            CreditCardRepository creditCardRepository) {

        this.customerRepository = customerRepository;
        this.creditCardRepository = creditCardRepository;
    }

    public List<CustomerOutstandingReportResponse>
            getCustomersWithHighestOutstanding() {

        return getCustomersByOutstanding(true);
    }

    public List<CustomerOutstandingReportResponse>
            getCustomersWithLowestOutstanding() {

        return getCustomersByOutstanding(false);
    }

    private List<CustomerOutstandingReportResponse>
            getCustomersByOutstanding(boolean highest) {

        List<Customer> customers = customerRepository
                .findAll(Sort.by(Sort.Direction.ASC, "customerId"));

        if (customers.isEmpty()) {
            return List.of();
        }

        Map<Long, BigDecimal> outstandingByCustomer = new HashMap<>();

        for (Customer customer : customers) {
            outstandingByCustomer.put(
                    customer.getCustomerId(),
                    BigDecimal.ZERO
            );
        }

        for (CreditCard card : creditCardRepository.findAll()) {
            outstandingByCustomer.merge(
                    card.getCustomerId(),
                    card.getOutstandingAmount(),
                    BigDecimal::add
            );
        }

        BigDecimal selectedOutstandingAmount;

        if (highest) {
            selectedOutstandingAmount = outstandingByCustomer.values()
                    .stream()
                    .max(BigDecimal::compareTo)
                    .orElseThrow();
        } else {
            selectedOutstandingAmount = outstandingByCustomer.values()
                    .stream()
                    .min(BigDecimal::compareTo)
                    .orElseThrow();
        }

        return customers.stream()
                .filter(customer ->
                        outstandingByCustomer
                                .get(customer.getCustomerId())
                                .compareTo(selectedOutstandingAmount) == 0
                )
                .map(customer ->
                        new CustomerOutstandingReportResponse(
                                customer.getCustomerId(),
                                customer.getCustomerName(),
                                outstandingByCustomer.get(
                                        customer.getCustomerId()
                                )
                        )
                )
                .toList();
    }
}