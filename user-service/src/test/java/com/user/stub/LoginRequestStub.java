package com.user.stub;

import com.user.dto.request.LoginRequest;

public class LoginRequestStub {

    private LoginRequestStub() {
    }

    public static LoginRequest buildRequest() {
        return LoginRequest.builder()
                .cpf("52998224725")
                .password("123456")
                .build();
    }
}