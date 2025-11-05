package com.bank_account.dto.response;

import lombok.Builder;

@Builder
public record BaseResponse<T>(
        T data,
        String message
) {
}
