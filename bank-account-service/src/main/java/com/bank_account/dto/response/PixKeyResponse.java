package com.bank_account.dto.response;

import com.bank_account.enums.PixKeyType;
import lombok.Builder;

@Builder
public record PixKeyResponse(
        String key,
        PixKeyType type
) {}