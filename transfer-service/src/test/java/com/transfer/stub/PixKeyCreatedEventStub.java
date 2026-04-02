package com.transfer.stub;

import com.transfer.event.PixKeyCreatedEvent;

public class PixKeyCreatedEventStub {

    private PixKeyCreatedEventStub() {
    }

    public static PixKeyCreatedEvent buildEvent() {
        return PixKeyCreatedEvent.builder()
                .userId("user-123")
                .accountNumber("123456")
                .pixKey("joao@email.com")
                .pixKeyType("EMAIL")
                .build();
    }
}