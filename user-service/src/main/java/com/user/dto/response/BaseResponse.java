package com.user.dto.response;

import lombok.Builder;

@Builder
public record BaseResponse<T>(
        T data,
        String message
) {
}
