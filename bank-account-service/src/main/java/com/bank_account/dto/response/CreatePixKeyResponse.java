package com.bank_account.dto.response;

import com.bank_account.enums.PixKeyType;
import lombok.Builder;

@Builder
public record CreatePixKeyResponse(
        String userId,
        PixKeyType type,
        String key
) {
}
