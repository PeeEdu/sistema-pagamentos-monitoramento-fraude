package com.transfer.stub;

import com.transfer.entity.PixTransfer;
import com.transfer.entity.Transfer;
import com.transfer.enums.PixKeyType;
import com.transfer.enums.TransferStatus;

import java.math.BigDecimal;
import java.time.Instant;

public class TransferStub {

    private TransferStub() {
    }

    public static Transfer buildEntity() {
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

    public static Transfer buildEntityWithStatus(TransferStatus status) {
        return PixTransfer.builder()
                .id("transfer-123")
                .fromAccountNumber("123456")
                .amount(new BigDecimal("100.00"))
                .status(status)
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

    public static Transfer buildEntityWithFailureReason(String failureReason) {
        return PixTransfer.builder()
                .id("transfer-123")
                .fromAccountNumber("123456")
                .amount(new BigDecimal("100.00"))
                .status(TransferStatus.FAILED)
                .description("Transferência PIX")
                .failureReason(failureReason)
                .createdAt(Instant.parse("2024-01-01T10:00:00Z"))
                .updatedAt(Instant.parse("2024-01-02T10:00:00Z"))
                .initiatedBy("user-123")
                .geoLocalization("-23.5505,-46.6333")
                .pixKey("joao@email.com")
                .pixKeyType(PixKeyType.EMAIL)
                .build();
    }

    public static PixTransfer buildPixTransfer() {
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