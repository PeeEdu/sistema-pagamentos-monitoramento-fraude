package com.bank_account.dto.response;

import com.bank_account.enums.AccountType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record BankAccountResponse(
        UUID id,
        String agency,
        String accountNumber,
        String owner,
        AccountType type,
        BigDecimal balance,
        Instant createdAt,
        Instant updatedAt
) {
}
