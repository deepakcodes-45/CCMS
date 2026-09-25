package com.ccms.merchant.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ccms.merchant.dto.MerchantRequest;
import com.ccms.merchant.dto.MerchantResponse;
import com.ccms.merchant.service.MerchantService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @PostMapping
    public ResponseEntity<MerchantResponse> createMerchant(
            @Valid @RequestBody MerchantRequest request) {

        MerchantResponse response = merchantService.createMerchant(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<MerchantResponse> getAllMerchants() {
        return merchantService.getAllMerchants();
    }

    @GetMapping("/{merchantId}")
    public MerchantResponse getMerchantById(
            @PathVariable Long merchantId) {

        return merchantService.getMerchantById(merchantId);
    }

    @PutMapping("/{merchantId}")
    public MerchantResponse updateMerchant(
            @PathVariable Long merchantId,
            @Valid @RequestBody MerchantRequest request) {

        return merchantService.updateMerchant(merchantId, request);
    }

    @DeleteMapping("/{merchantId}")
    public ResponseEntity<Void> deleteMerchant(
            @PathVariable Long merchantId) {

        merchantService.deleteMerchant(merchantId);

        return ResponseEntity.noContent().build();
    }
}