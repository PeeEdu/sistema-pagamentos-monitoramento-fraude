package com.user.stub;

import com.user.event.PasswordResetRequestedEvent;

import java.time.LocalDateTime;

public class PasswordResetEventStub {

    public static PasswordResetRequestedEvent buildEvent() {
        return new PasswordResetRequestedEvent(
                "user-123",
                "user@email.com",
                "reset-token-123",
                LocalDateTime.now(),
                "João Silva"
        );
    }

    public static PasswordResetRequestedEvent buildEventWithCustomData(String userId, String email) {
        return new PasswordResetRequestedEvent(
                userId,
                email,
                "reset-token-456",
                LocalDateTime.now(),
                "Maria Silva"
        );
    }
}