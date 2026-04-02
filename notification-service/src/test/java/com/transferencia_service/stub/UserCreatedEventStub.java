package com.transferencia_service.stub;

import com.transferencia_service.event.UserCreatedEvent;

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