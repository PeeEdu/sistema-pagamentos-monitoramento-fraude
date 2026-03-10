package com.transfer.dto.request;

import com.transfer.enums.PixKeyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PixKeyRequestDto(
        @NotNull(message = "Tipo de chave PIX é obrigatório")
        PixKeyType type,

        @NotBlank(message = "Chave PIX é obrigatória")
        String key
) {
}
