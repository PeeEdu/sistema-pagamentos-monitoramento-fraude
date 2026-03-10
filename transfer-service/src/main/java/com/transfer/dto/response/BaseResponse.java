package com.transfer.dto.response;

import lombok.Builder;

@Builder
public record BaseResponse<T>(
        T data,
        String message
) {
}
