package com.bank_account.stub;

import com.bank_account.entity.PixKey;
import com.bank_account.enums.PixKeyType;

public class PixKeyStub {

    private PixKeyStub() {
    }

    public static PixKey buildEntity() {
        return PixKey.builder()
                .type(PixKeyType.EMAIL)
                .key("joao.silva@email.com")
                .build();
    }

    public static PixKey buildEntity(String key) {
        return PixKey.builder()
                .type(PixKeyType.EMAIL)
                .key(key)
                .build();
    }
}