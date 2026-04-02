package com.bank_account.stub;

import com.bank_account.event.PixKeyDeletedEvent;

import java.time.Instant;

public class PixKeyDeletedEventStub {

    private PixKeyDeletedEventStub() {
    }

    public static PixKeyDeletedEvent buildEvent() {
        return PixKeyDeletedEvent.builder()
                .userId("user-123")
                .accountNumber("123456")
                .pixKey("joao.silva@email.com")
                .deletedAt(Instant.parse("2024-01-01T10:00:00Z"))
                .build();
    }
}