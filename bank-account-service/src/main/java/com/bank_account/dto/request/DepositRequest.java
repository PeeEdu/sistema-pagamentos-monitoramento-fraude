package com.bank_account.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositRequest(
        @NotNull
        BigDecimal balance
) {
}
