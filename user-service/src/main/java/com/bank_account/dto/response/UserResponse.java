package com.bank_account.dto.response;


import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        String id,
        String name,
        String email,
        String password,
        String cpf,
        String phone,
        Instant createdAt,
        Instant updatedAt
) {
}
