package com.bank_account.dto.request;

import com.bank_account.enums.AccountType;
import jakarta.validation.constraints.NotBlank;

public record CreateAccountRequest(
        @NotBlank
        String agency,
        @NotBlank
        String number,
        @NotBlank
        String owner,
        @NotBlank
        AccountType type
) {
}
