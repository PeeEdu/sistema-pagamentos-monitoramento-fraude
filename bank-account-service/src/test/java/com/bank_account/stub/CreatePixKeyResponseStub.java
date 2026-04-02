package com.bank_account.stub;

import com.bank_account.dto.response.CreatePixKeyResponse;
import com.bank_account.enums.PixKeyType;

public class CreatePixKeyResponseStub {

    private CreatePixKeyResponseStub() {
    }

    public static CreatePixKeyResponse buildResponse() {
        return CreatePixKeyResponse.builder()
                .userId("user-123")
                .type(PixKeyType.EMAIL)
                .key("joao.silva@email.com")
                .build();
    }
}