package com.ccms.transactionreport.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ccms.transactionreport.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}