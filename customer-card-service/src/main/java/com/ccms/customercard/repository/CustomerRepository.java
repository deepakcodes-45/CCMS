package com.ccms.customercard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ccms.customercard.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);

    boolean existsByMobileNumber(String mobileNumber);

    boolean existsByPanNumber(String panNumber);
}