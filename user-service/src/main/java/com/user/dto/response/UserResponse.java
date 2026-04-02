package com.user.dto.response;


import lombok.Builder;

import java.time.Instant;

@Builder
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
