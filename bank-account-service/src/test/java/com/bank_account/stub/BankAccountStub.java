package com.bank_account.stub;

import com.bank_account.entity.BankAccount;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class BankAccountStub {

    private BankAccountStub() {
    }

    public static BankAccount buildEntity() {
        return BankAccount.builder()
                .id("account-123")
                .userId("user-123")
                .accountNumber("123456")
                .balance(new BigDecimal("1000.00"))
                .accountType("CORRENTE")
                .status("ACTIVE")
                .currency("BRL")
                .pixKey(List.of(
                        PixKeyStub.buildEntity("joao.silva@email.com"),
                        PixKeyStub.buildEntity("11999999999")
                ))
                .createdAt(Instant.parse("2024-01-01T10:00:00Z"))
                .updatedAt(Instant.parse("2024-01-02T10:00:00Z"))
                .build();
    }

    public static BankAccount buildEntityWithoutPixKeys() {
        return BankAccount.builder()
                .id("account-123")
                .userId("user-123")
                .accountNumber("123456")
                .balance(new BigDecimal("1000.00"))
                .accountType("CORRENTE")
                .status("ACTIVE")
                .currency("BRL")
                .pixKey(null)
                .createdAt(Instant.parse("2024-01-01T10:00:00Z"))
                .updatedAt(Instant.parse("2024-01-02T10:00:00Z"))
                .build();
    }
}