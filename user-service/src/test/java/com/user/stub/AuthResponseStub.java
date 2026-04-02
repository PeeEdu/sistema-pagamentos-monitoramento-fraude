package com.user.stub;

import com.user.dto.response.AuthResponse;

public class AuthResponseStub {

    private AuthResponseStub() {
    }

    public static AuthResponse buildResponse() {
        return AuthResponse.builder()
                .token("mocked-jwt-token")
                .build();
    }
}