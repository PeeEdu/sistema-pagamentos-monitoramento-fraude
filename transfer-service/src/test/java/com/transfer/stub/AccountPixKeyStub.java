package com.transfer.stub;

import com.transfer.entity.AccountPixKey;

public class AccountPixKeyStub {

    private AccountPixKeyStub() {
    }

    public static AccountPixKey buildEntity() {
        return AccountPixKey.builder()
                .id("pix-123")
                .userId("user-123")
                .accountNumber("123456")
                .pixKey("joao@email.com")
                .pixKeyType("EMAIL")
                .build();
    }

    public static AccountPixKey buildEntity(String pixKey) {
        return AccountPixKey.builder()
                .id("pix-123")
                .userId("user-123")
                .accountNumber("123456")
                .pixKey(pixKey)
                .pixKeyType("EMAIL")
                .build();
    }
}