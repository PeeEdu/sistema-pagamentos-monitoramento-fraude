package com.transferencia_service.stub;

import com.transferencia_service.event.PasswordResetRequestedEvent;

public class PasswordResetRequestedEventStub {

    private PasswordResetRequestedEventStub() {
    }

    public static PasswordResetRequestedEvent buildEvent() {
        return PasswordResetRequestedEvent.builder()
                .userId("user-123")
                .email("joao.silva@email.com")
                .resetToken("reset-token-123")
                .userName("João Silva")
                .build();
    }

    public static PasswordResetRequestedEvent buildEventWithoutUserName() {
        return PasswordResetRequestedEvent.builder()
                .userId("user-123")
                .email("joao.silva@email.com")
                .resetToken("reset-token-123")
                .userName(null)
                .build();
    }
}