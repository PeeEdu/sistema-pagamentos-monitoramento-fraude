package com.user.stub;

import com.user.dto.request.ChangePasswordRequest;

public class ChangePasswordRequestStub {

    private ChangePasswordRequestStub() {
    }

    public static ChangePasswordRequest buildRequest() {
        return ChangePasswordRequest.builder()
                .token("reset-token-123")
                .newPassword("NovaSenha@123")
                .build();
    }
}