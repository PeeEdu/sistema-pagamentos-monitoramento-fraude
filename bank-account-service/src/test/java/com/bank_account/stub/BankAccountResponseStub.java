package com.bank_account.stub;

import com.bank_account.dto.response.BankAccountResponse;

import java.math.BigDecimal;
import java.time.Instant;

public class BankAccountResponseStub {

    private BankAccountResponseStub() {
    }

    public static BankAccountResponse buildResponse() {
        return new BankAccountResponse(
                "account-123",
                "user-123",
                "123456",
                new BigDecimal("1000.00"),
                "CHECKING",
                "ACTIVE",
                "BRL",
                Instant.parse("2024-01-01T10:00:00Z")
        );
    }
}