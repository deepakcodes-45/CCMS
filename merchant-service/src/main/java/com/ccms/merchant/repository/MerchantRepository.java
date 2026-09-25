package com.ccms.merchant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ccms.merchant.entity.Merchant;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {

}