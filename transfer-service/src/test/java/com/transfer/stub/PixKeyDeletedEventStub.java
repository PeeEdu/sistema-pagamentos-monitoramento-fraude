package com.transfer.stub;

import com.transfer.event.PixKeyDeletedEvent;

public class PixKeyDeletedEventStub {

    private PixKeyDeletedEventStub() {
    }

    public static PixKeyDeletedEvent buildEvent() {
        return PixKeyDeletedEvent.builder()
                .pixKey("joao@email.com")
                .build();
    }
}