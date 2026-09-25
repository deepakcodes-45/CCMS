package com.ccms.customercard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ccms.customercard.entity.CreditCard;

public interface CreditCardRepository extends JpaRepository<CreditCard, String> {

    List<CreditCard> findByCustomerCustomerId(Long customerId);
    boolean existsByCustomerCustomerId(Long customerId);
}