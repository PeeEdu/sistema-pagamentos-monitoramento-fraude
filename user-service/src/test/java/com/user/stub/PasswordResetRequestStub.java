package com.user.stub;

import com.user.dto.request.PasswordResetRequest;

public class PasswordResetRequestStub {

    private PasswordResetRequestStub() {
    }

    public static PasswordResetRequest buildRequest() {
        return PasswordResetRequest.builder()
                .email("joao.silva@email.com")
                .build();
    }
}