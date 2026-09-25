package com.ccms.customercard.dto;

public record CustomerResponse(
        Long customerId,
        String customerName,
        String email,
        String mobileNumber
) {
}