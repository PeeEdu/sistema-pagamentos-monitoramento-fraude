package com.user.stub;

import com.user.workflow.dto.UserRegistrationData;

public class UserRegistrationDataStub {

    private UserRegistrationDataStub() {
    }

    public static UserRegistrationData buildResponse() {
        return UserRegistrationData.builder()
                .name("João Silva")
                .email("joao.silva@email.com")
                .cpf("52998224725")
                .phone("11999999999")
                .password("123456")
                .build();
    }
}