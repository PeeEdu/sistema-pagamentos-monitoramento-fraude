package com.bank_account.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record BankAccountResponse(
        String id,
        String userId,
        String accountNumber,
        BigDecimal balance,
        String accountType,
        String status,
        String currency,
        Instant createdAt
) {
}
