package com.bank_account.stub;

import com.bank_account.event.PixKeyCreatedEvent;

import java.time.Instant;

public class PixKeyCreatedEventStub {

    private PixKeyCreatedEventStub() {
    }

    public static PixKeyCreatedEvent buildEvent() {
        return PixKeyCreatedEvent.builder()
                .userId("user-123")
                .accountNumber("123456")
                .pixKey("joao.silva@email.com")
                .pixKeyType("EMAIL")
                .createdAt(Instant.parse("2024-01-01T10:00:00Z"))
                .build();
    }
}