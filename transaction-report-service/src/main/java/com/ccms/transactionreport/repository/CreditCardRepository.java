package com.ccms.transactionreport.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ccms.transactionreport.entity.CreditCard;

public interface CreditCardRepository extends JpaRepository<CreditCard, String> {

}