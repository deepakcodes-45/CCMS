package com.ccms.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ccms.auth.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}