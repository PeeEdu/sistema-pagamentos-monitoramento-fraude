package com.bank_account.dto.request;

import com.bank_account.enums.PixKeyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreatePixKeyRequest(
        @NotNull
        PixKeyType type,

        @NotBlank
        String key
) {
}
