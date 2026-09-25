package com.ccms.transactionreport.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ccms.transactionreport.dto.PaymentRequest;
import com.ccms.transactionreport.dto.PurchaseRequest;
import com.ccms.transactionreport.dto.TransactionResponse;
import com.ccms.transactionreport.security.JwtUserPrincipal;
import com.ccms.transactionreport.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/purchases")
    public ResponseEntity<TransactionResponse> makePurchase(
            @Valid @RequestBody PurchaseRequest request,
            @AuthenticationPrincipal JwtUserPrincipal currentUser) {

        TransactionResponse response =
                transactionService.makePurchase(request, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/payments")
    public ResponseEntity<TransactionResponse> makePayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal JwtUserPrincipal currentUser) {

        TransactionResponse response =
                transactionService.makePayment(request, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}