package com.transfer.stub;

import com.transfer.entity.PixTransfer;
import com.transfer.enums.PixKeyType;
import com.transfer.enums.TransferStatus;

import java.math.BigDecimal;
import java.time.Instant;

public class PixTransferStub {

    private PixTransferStub() {
    }

    public static PixTransfer buildEntity() {
        return PixTransfer.builder()
                .id("transfer-123")
                .fromAccountNumber("123456")
                .amount(new BigDecimal("100.00"))
                .status(TransferStatus.COMPLETED)
                .description("Transferência PIX")
                .failureReason(null)
                .createdAt(Instant.parse("2024-01-01T10:00:00Z"))
                .updatedAt(Instant.parse("2024-01-02T10:00:00Z"))
                .initiatedBy("user-123")
                .geoLocalization("-23.5505,-46.6333")
                .pixKey("joao@email.com")
                .pixKeyType(PixKeyType.EMAIL)
                .build();
    }
}