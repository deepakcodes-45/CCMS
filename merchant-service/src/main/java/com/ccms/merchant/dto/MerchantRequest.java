package com.ccms.merchant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MerchantRequest(

        @NotBlank(message = "Merchant name is required")
        @Size(max = 100, message = "Merchant name must not exceed 100 characters")
        String merchantName,

        @NotBlank(message = "Category is required")
        @Size(max = 50, message = "Category must not exceed 50 characters")
        String category,

        @NotBlank(message = "Location is required")
        @Size(max = 100, message = "Location must not exceed 100 characters")
        String location
) {
}