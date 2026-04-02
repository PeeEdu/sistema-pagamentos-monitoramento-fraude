package com.transfer.stub;

import com.transfer.dto.request.CreatePixTransferRequest;
import com.transfer.dto.request.PixKeyRequestDto;
import com.transfer.enums.PixKeyType;

import java.math.BigDecimal;

public class CreatePixTransferRequestStub {

    private CreatePixTransferRequestStub() {
    }

    public static CreatePixTransferRequest buildRequest() {
        return CreatePixTransferRequest.builder()
                .fromAccountNumber("123456")
                .pix(PixKeyRequestDto.builder()
                        .type(PixKeyType.EMAIL)
                        .key("joao@email.com")
                        .build())
                .amount(new BigDecimal("100.00"))
                .description("Transferência PIX")
                .initiatedBy("user-123")
                .geoLocalization("-23.5505,-46.6333")
                .build();
    }
}