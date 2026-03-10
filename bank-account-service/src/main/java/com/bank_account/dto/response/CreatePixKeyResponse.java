package com.bank_account.dto.response;

import com.bank_account.PixKeyType;
import lombok.Builder;

@Builder
public record CreatePixKeyResponse(
        String userId,
        PixKeyType type,
        String key
) {
}
