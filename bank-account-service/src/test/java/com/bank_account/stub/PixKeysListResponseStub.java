package com.bank_account.stub;

import com.bank_account.dto.response.PixKeyResponse;
import com.bank_account.dto.response.PixKeysListResponse;
import com.bank_account.enums.PixKeyType;

import java.util.List;

public class PixKeysListResponseStub {

    private PixKeysListResponseStub() {
    }

    public static PixKeysListResponse buildResponse() {
        return new PixKeysListResponse(
                "user-123",
                "123456",
                List.of(
                        PixKeyResponse.builder()
                                .key("joao.silva@email.com")
                                .type(PixKeyType.EMAIL)
                                .build(),
                        PixKeyResponse.builder()
                                .key("11999999999")
                                .type(PixKeyType.PHONE)
                                .build()
                ),
                2
        );
    }
}