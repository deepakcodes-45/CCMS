package com.ccms.transactionreport.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ccms.transactionreport.entity.CardTransaction;
import org.springframework.data.repository.query.Param;
public interface CardTransactionRepository
        extends JpaRepository<CardTransaction, Long> {

    List<CardTransaction> findByCardNumberOrderByTransactionTimestampDesc(
            String cardNumber
    );

    @Query(value = """
            SELECT NVL(SUM(amount), 0)
            FROM card_transaction
            WHERE transaction_type = 'PURCHASE'
              AND transaction_status = 'SUCCESS'
              AND transaction_timestamp >= CAST(TRUNC(SYSDATE) AS TIMESTAMP)
              AND transaction_timestamp < CAST(TRUNC(SYSDATE) + 1 AS TIMESTAMP)
            """, nativeQuery = true)
    BigDecimal getTodaySuccessfulPurchaseTotal();

    @Query(value = """
            SELECT NVL(SUM(amount), 0)
            FROM card_transaction
            WHERE transaction_type = 'PAYMENT'
              AND transaction_status = 'SUCCESS'
              AND transaction_timestamp >= CAST(TRUNC(SYSDATE) AS TIMESTAMP)
              AND transaction_timestamp < CAST(TRUNC(SYSDATE) + 1 AS TIMESTAMP)
            """, nativeQuery = true)
    BigDecimal getTodaySuccessfulPaymentTotal();

    @Query(value = """
            SELECT NVL(AVG(amount), 0)
            FROM card_transaction
            WHERE transaction_type = 'PURCHASE'
              AND transaction_status = 'SUCCESS'
            """, nativeQuery = true)
    BigDecimal getAverageSuccessfulPurchaseAmount();

    @Query(value = """
            SELECT *
            FROM card_transaction
            WHERE transaction_type = 'PURCHASE'
              AND transaction_status = 'SUCCESS'
              AND amount = (
                    SELECT MAX(amount)
                    FROM card_transaction
                    WHERE transaction_type = 'PURCHASE'
                      AND transaction_status = 'SUCCESS'
              )
            ORDER BY transaction_id
            """, nativeQuery = true)
    List<CardTransaction> findLargestSuccessfulPurchases();
    
    @Query(value = """
            SELECT ct.*
            FROM card_transaction ct
            JOIN credit_card cc
              ON cc.card_number = ct.card_number
            WHERE cc.customer_id = :customerId
            ORDER BY ct.transaction_timestamp DESC, ct.transaction_id DESC
            """, nativeQuery = true)
    List<CardTransaction> findAllByCustomerIdOrderByTimestampDesc(
            @Param("customerId") Long customerId
    );
}