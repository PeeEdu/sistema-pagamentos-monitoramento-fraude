package com.user.stub;


import com.user.event.UserCreatedEvent;

public class UserCreatedEventStub {

    private UserCreatedEventStub() {
    }

    public static UserCreatedEvent buildEvent() {
        return UserCreatedEvent.builder()
                .userId("user-123")
                .name("João Silva")
                .email("joao.silva@email.com")
                .build();
    }
}