package com.bank_account.stub;

import com.bank_account.dto.request.CreatePixKeyRequest;
import com.bank_account.enums.PixKeyType;

public class CreatePixKeyRequestStub {

    private CreatePixKeyRequestStub() {
    }

    public static CreatePixKeyRequest buildRequest() {
        return CreatePixKeyRequest.builder()
                .type(PixKeyType.EMAIL)
                .key("joao.silva@email.com")
                .build();
    }
}