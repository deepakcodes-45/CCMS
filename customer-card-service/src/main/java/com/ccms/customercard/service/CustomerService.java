package com.ccms.customercard.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccms.customercard.dto.CustomerRequest;
import com.ccms.customercard.dto.CustomerResponse;
import com.ccms.customercard.entity.Customer;
import com.ccms.customercard.exception.BusinessRuleException;
import com.ccms.customercard.exception.DuplicateResourceException;
import com.ccms.customercard.exception.ResourceNotFoundException;
import com.ccms.customercard.repository.CreditCardRepository;
import com.ccms.customercard.repository.CustomerRepository;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CreditCardRepository creditCardRepository;

    public CustomerService(
            CustomerRepository customerRepository,
            CreditCardRepository creditCardRepository) {

        this.customerRepository = customerRepository;
        this.creditCardRepository = creditCardRepository;
    }

    public CustomerResponse createCustomer(CustomerRequest request) {
        validateUniqueFields(request, null);

        Customer customer = new Customer();
        copyRequestToCustomer(request, customer);

        return toResponse(customerRepository.save(customer));
    }

    public CustomerResponse getCustomerById(Long customerId) {
        return toResponse(findCustomer(customerId));
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CustomerResponse updateCustomer(Long customerId, CustomerRequest request) {
        Customer customer = findCustomer(customerId);

        validateUniqueFields(request, customer);
        copyRequestToCustomer(request, customer);

        return toResponse(customerRepository.save(customer));
    }

    public void deleteCustomer(Long customerId) {
        Customer customer = findCustomer(customerId);

        if (creditCardRepository.existsByCustomerCustomerId(customerId)) {
            throw new BusinessRuleException(
                    "Customer cannot be deleted because credit cards exist"
            );
        }

        customerRepository.delete(customer);
    }

    private Customer findCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + customerId
                ));
    }

    private void validateUniqueFields(
            CustomerRequest request,
            Customer existingCustomer) {

        boolean emailChanged = existingCustomer == null
                || !existingCustomer.getEmail().equals(request.email());

        if (emailChanged && customerRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        boolean mobileChanged = existingCustomer == null
                || !existingCustomer.getMobileNumber().equals(request.mobileNumber());

        if (mobileChanged
                && customerRepository.existsByMobileNumber(request.mobileNumber())) {
            throw new DuplicateResourceException("Mobile number already exists");
        }

        boolean panChanged = existingCustomer == null
                || !existingCustomer.getPanNumber().equals(request.panNumber());

        if (panChanged && customerRepository.existsByPanNumber(request.panNumber())) {
            throw new DuplicateResourceException("PAN number already exists");
        }
    }

    private void copyRequestToCustomer(
            CustomerRequest request,
            Customer customer) {

        customer.setCustomerName(request.customerName());
        customer.setEmail(request.email());
        customer.setMobileNumber(request.mobileNumber());
        customer.setPanNumber(request.panNumber());
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getEmail(),
                customer.getMobileNumber()
        );
    }
}