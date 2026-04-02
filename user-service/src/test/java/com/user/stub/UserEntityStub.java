package com.user.stub;

import com.user.entities.UserEntity;

import java.time.Instant;

public class UserEntityStub {

    private UserEntityStub() {
    }

    public static UserEntity buildEntity() {
        return UserEntity.builder()
                .id("user-123")
                .name("João Silva")
                .email("joao.silva@email.com")
                .password("senha-criptografada")
                .cpf("52998224725")
                .phone("11999999999")
                .active(true)
                .createdAt(Instant.parse("2024-01-01T10:00:00Z"))
                .updatedAt(Instant.parse("2024-01-02T10:00:00Z"))
                .build();
    }

    public static UserEntity buildEntityWithDates(Instant createdAt, Instant updatedAt) {
        return UserEntity.builder()
                .id("user-123")
                .name("João Silva")
                .email("joao.silva@email.com")
                .password("senha-criptografada")
                .cpf("52998224725")
                .phone("11999999999")
                .active(true)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public static UserEntity buildInactiveEntity() {
        return UserEntity.builder()
                .id("user-123")
                .name("João Silva")
                .email("joao.silva@email.com")
                .password("senha-criptografada")
                .cpf("52998224725")
                .phone("11999999999")
                .active(false)
                .createdAt(Instant.parse("2024-01-01T10:00:00Z"))
                .updatedAt(Instant.parse("2024-01-02T10:00:00Z"))
                .build();
    }

    public static UserEntity buildEntityWithoutId() {
        return UserEntity.builder()
                .name("João Silva")
                .email("joao.silva@email.com")
                .password("senha-criptografada")
                .cpf("52998224725")
                .phone("11999999999")
                .active(true)
                .createdAt(Instant.parse("2024-01-01T10:00:00Z"))
                .updatedAt(Instant.parse("2024-01-02T10:00:00Z"))
                .build();
    }
}