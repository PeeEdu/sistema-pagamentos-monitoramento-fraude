package com.user.dto.response;


import java.time.Instant;

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
