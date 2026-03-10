package com.transfer.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreatePixTransferRequest(
        @NotBlank(message = "Conta de origem é obrigatória")
        String fromAccountId,

        @NotNull(message = "Chave PIX é obrigatória")
        PixKeyRequestDto pix,

        @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
        BigDecimal amount,

        String description,

        @NotBlank(message = "ID do usuário é obrigatório")
        String initiatedBy
) {
}
