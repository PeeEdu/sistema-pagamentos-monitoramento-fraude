package com.user.stub;

import com.user.dto.request.RegisterRequest;

public class RegisterRequestStub {

    private RegisterRequestStub() {
    }

    public static RegisterRequest buildRequest() {
        return RegisterRequest.builder()
                .name("João Silva")
                .email("joao.silva@email.com")
                .cpf("52998224725")
                .phone("11999999999")
                .password("123456")
                .build();
    }
}