package com.user.dto.response;

import lombok.Builder;

@Builder
public record UserCreateResponse(
        String id,
        String name,
        String email,
        String phone
) {
}
