package com.user.stub;

import com.user.workflow.dto.UserData;

public class UserDataStub {

    private UserDataStub() {
    }

    public static UserData buildResponse() {
        return UserData.builder()
                .id("user-123")
                .name("João Silva")
                .email("joao.silva@email.com")
                .build();
    }
}