package com.ccms.customercard.dto;

import com.ccms.customercard.enums.CardStatus;

import jakarta.validation.constraints.NotNull;

public record CardStatusUpdateRequest(
        @NotNull
        CardStatus cardStatus
) {
}