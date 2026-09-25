package com.ccms.transactionreport.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ccms.transactionreport.entity.Merchant;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {

}