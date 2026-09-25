package com.ccms.customercard.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ccms.customercard.dto.CardIssueRequest;
import com.ccms.customercard.dto.CardResponse;
import com.ccms.customercard.dto.CardStatusUpdateRequest;
import com.ccms.customercard.dto.CardUpdateRequest;
import com.ccms.customercard.security.JwtUserPrincipal;
import com.ccms.customercard.security.RoleName;
import com.ccms.customercard.service.CreditCardService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cards")
public class CreditCardController {

    private final CreditCardService creditCardService;

    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @PostMapping
    public ResponseEntity<CardResponse> issueCard(
            @Valid @RequestBody CardIssueRequest request) {

        CardResponse response = creditCardService.issueCard(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<CardResponse> getAllCards() {
        return creditCardService.getAllCards();
    }

    @GetMapping("/customer/{customerId}")
    public List<CardResponse> getCardsByCustomerId(
            @PathVariable Long customerId,
            @AuthenticationPrincipal JwtUserPrincipal currentUser) {

        ensureCanAccessCustomer(customerId, currentUser);

        return creditCardService.getCardsByCustomerId(customerId);
    }

    @GetMapping("/{cardNumber}")
    public CardResponse getCardByNumber(
            @PathVariable String cardNumber,
            @AuthenticationPrincipal JwtUserPrincipal currentUser) {

        CardResponse card = creditCardService.getCardByNumber(cardNumber);

        ensureCanAccessCard(card, currentUser);

        return card;
    }

    @PutMapping("/{cardNumber}")
    public CardResponse updateCard(
            @PathVariable String cardNumber,
            @Valid @RequestBody CardUpdateRequest request) {

        return creditCardService.updateCard(cardNumber, request);
    }

    @PatchMapping("/{cardNumber}/status")
    public CardResponse updateCardStatus(
            @PathVariable String cardNumber,
            @Valid @RequestBody CardStatusUpdateRequest request) {

        return creditCardService.updateCardStatus(cardNumber, request);
    }

    private void ensureCanAccessCustomer(
            Long requestedCustomerId,
            JwtUserPrincipal currentUser
    ) {
        if (currentUser.role() == RoleName.ADMIN) {
            return;
        }

        if (!requestedCustomerId.equals(currentUser.customerId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can access only your own cards"
            );
        }
        
    }
    private void ensureCanAccessCard(
            CardResponse card,
            JwtUserPrincipal currentUser
    ) {
        if (currentUser.role() == RoleName.ADMIN) {
            return;
        }

        if (!card.customerId().equals(currentUser.customerId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can access only your own cards"
            );
        }
    }
}