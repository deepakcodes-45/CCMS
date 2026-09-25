package com.ccms.transactionreport.service;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ccms.transactionreport.dto.PaymentRequest;
import com.ccms.transactionreport.dto.PurchaseRequest;
import com.ccms.transactionreport.dto.TransactionResponse;
import com.ccms.transactionreport.entity.CardTransaction;
import com.ccms.transactionreport.entity.CreditCard;
import com.ccms.transactionreport.entity.Merchant;
import com.ccms.transactionreport.enums.CardStatus;
import com.ccms.transactionreport.enums.TransactionStatus;
import com.ccms.transactionreport.enums.TransactionType;
import com.ccms.transactionreport.exception.BusinessRuleException;
import com.ccms.transactionreport.exception.ResourceNotFoundException;
import com.ccms.transactionreport.repository.CardTransactionRepository;
import com.ccms.transactionreport.repository.CreditCardRepository;
import com.ccms.transactionreport.repository.MerchantRepository;
import com.ccms.transactionreport.security.JwtUserPrincipal;
import com.ccms.transactionreport.security.RoleName;

@Service
@Transactional
public class TransactionService {

    private final CreditCardRepository creditCardRepository;
    private final MerchantRepository merchantRepository;
    private final CardTransactionRepository cardTransactionRepository;

    public TransactionService(
            CreditCardRepository creditCardRepository,
            MerchantRepository merchantRepository,
            CardTransactionRepository cardTransactionRepository) {

        this.creditCardRepository = creditCardRepository;
        this.merchantRepository = merchantRepository;
        this.cardTransactionRepository = cardTransactionRepository;
    }

    public TransactionResponse makePurchase(
            PurchaseRequest request,
            JwtUserPrincipal currentUser) {

        CreditCard card = findCard(request.cardNumber());

        ensureCanUseCard(card, currentUser);

        Merchant merchant = merchantRepository.findById(request.merchantId())
                .orElse(null);

        /*
         * An invalid merchant ID cannot be stored because MERCHANT_ID is
         * a foreign key. Therefore, save the failed purchase with null
         * merchantId and a clear failure reason.
         */
        if (merchant == null) {
            return saveFailedPurchase(
                    card,
                    request,
                    null,
                    "Merchant not found with ID: " + request.merchantId()
            );
        }

        if (card.getCardStatus() != CardStatus.ACTIVE) {
            return saveFailedPurchase(
                    card,
                    request,
                    merchant.getMerchantId(),
                    "Card is blocked"
            );
        }

        if (card.getAvailableCredit().compareTo(request.amount()) < 0) {
            return saveFailedPurchase(
                    card,
                    request,
                    merchant.getMerchantId(),
                    "Insufficient available credit"
            );
        }

        card.setAvailableCredit(
                card.getAvailableCredit().subtract(request.amount())
        );

        card.setOutstandingAmount(
                card.getOutstandingAmount().add(request.amount())
        );

        creditCardRepository.save(card);

        CardTransaction transaction = createTransaction(
                card.getCardNumber(),
                TransactionType.PURCHASE,
                request.amount(),
                merchant.getMerchantId(),
                TransactionStatus.SUCCESS,
                null
        );

        CardTransaction savedTransaction =
                cardTransactionRepository.save(transaction);

        return toResponse(savedTransaction, card);
    }

    public TransactionResponse makePayment(
            PaymentRequest request,
            JwtUserPrincipal currentUser) {

        CreditCard card = findCard(request.cardNumber());

        ensureCanUseCard(card, currentUser);

        if (request.amount().compareTo(card.getOutstandingAmount()) > 0) {
            throw new BusinessRuleException(
                    "Payment amount cannot exceed outstanding amount"
            );
        }

        card.setOutstandingAmount(
                card.getOutstandingAmount().subtract(request.amount())
        );

        card.setAvailableCredit(
                card.getAvailableCredit().add(request.amount())
        );

        creditCardRepository.save(card);

        CardTransaction transaction = createTransaction(
                card.getCardNumber(),
                TransactionType.PAYMENT,
                request.amount(),
                null,
                TransactionStatus.SUCCESS,
                null
        );

        CardTransaction savedTransaction =
                cardTransactionRepository.save(transaction);

        return toResponse(savedTransaction, card);
    }

    private void ensureCanUseCard(
            CreditCard card,
            JwtUserPrincipal currentUser) {

        if (currentUser == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication is required"
            );
        }

        if (currentUser.role() == RoleName.ADMIN) {
            return;
        }

        if (currentUser.customerId() == null
                || !card.getCustomerId().equals(currentUser.customerId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can make transactions only on your own cards"
            );
        }
    }

    private CreditCard findCard(String cardNumber) {
        return creditCardRepository.findById(cardNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Card not found: " + cardNumber
                        )
                );
    }

    private TransactionResponse saveFailedPurchase(
            CreditCard card,
            PurchaseRequest request,
            Long merchantId,
            String failureReason) {

        CardTransaction transaction = createTransaction(
                card.getCardNumber(),
                TransactionType.PURCHASE,
                request.amount(),
                merchantId,
                TransactionStatus.FAILED,
                failureReason
        );

        CardTransaction savedTransaction =
                cardTransactionRepository.save(transaction);

        return toResponse(savedTransaction, card);
    }

    private CardTransaction createTransaction(
            String cardNumber,
            TransactionType transactionType,
            BigDecimal amount,
            Long merchantId,
            TransactionStatus transactionStatus,
            String failureReason) {

        CardTransaction transaction = new CardTransaction();

        transaction.setCardNumber(cardNumber);
        transaction.setTransactionType(transactionType);
        transaction.setAmount(amount);
        transaction.setMerchantId(merchantId);
        transaction.setTransactionStatus(transactionStatus);
        transaction.setFailureReason(failureReason);

        return transaction;
    }

    private TransactionResponse toResponse(
            CardTransaction transaction,
            CreditCard card) {

        return new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getCardNumber(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getMerchantId(),
                transaction.getTransactionTimestamp(),
                transaction.getTransactionStatus(),
                transaction.getFailureReason(),
                card.getAvailableCredit(),
                card.getOutstandingAmount()
        );
    }
}