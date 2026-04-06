package com.user.stub;

import com.user.entities.PasswordResetAuditEntity;

import java.time.LocalDateTime;

public class PasswordResetAuditEntityStub {

    private PasswordResetAuditEntityStub() {
    }

    public static PasswordResetAuditEntity buildEntity() {
        return PasswordResetAuditEntity.builder()
                .userId("user-123")
                .email("joao.silva@email.com")
                .token("reset-token-123")
                .status("REQUESTED")
                .requestedAt(LocalDateTime.of(2024, 1, 1, 10, 0, 0))
                .expiresAt(LocalDateTime.of(2024, 1, 1, 11, 0, 0))
                .build();
    }
}