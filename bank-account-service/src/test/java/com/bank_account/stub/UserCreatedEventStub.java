package com.bank_account.stub;

import com.bank_account.event.UserCreatedEvent;

import java.time.LocalDateTime;

public class UserCreatedEventStub {

    private UserCreatedEventStub() {
    }

    public static UserCreatedEvent buildEvent() {
        return UserCreatedEvent.builder()
                .userId("user-123")
                .name("João Silva")
                .email("joao.silva@email.com")
                .createdAt(LocalDateTime.now())
                .build();
    }
}