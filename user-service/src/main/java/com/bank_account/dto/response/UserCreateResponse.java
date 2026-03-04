package com.bank_account.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserCreateResponse(
        String id,
        String name,
        String email,
        String phone
) {
}
