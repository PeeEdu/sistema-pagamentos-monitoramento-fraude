package com.fraud.stub;

import com.fraud.enums.PixKeyType;
import com.fraud.event.TransferInitiatedEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferInitiatedEventStub {

    private TransferInitiatedEventStub() {
    }

    public static TransferInitiatedEvent buildEvent() {
        return TransferInitiatedEvent.builder()
                .transferId("transfer-123")
                .fromAccountNumber("123456")
                .pixKeyType(PixKeyType.EMAIL)
                .pixKey("joao.silva@email.com")
                .amount(new BigDecimal("1000.00"))
                .description("Transferência PIX")
                .initiatedBy("user-123")
                .geoLocalization("-23.5505,-46.6333")
                .initiatedAt(LocalDateTime.of(2024, 1, 1, 10, 0, 0))
                .build();
    }

    public static TransferInitiatedEvent buildHighAmountEvent() {
        return TransferInitiatedEvent.builder()
                .transferId("transfer-123")
                .fromAccountNumber("123456")
                .pixKeyType(PixKeyType.EMAIL)
                .pixKey("joao.silva@email.com")
                .amount(new BigDecimal("50000.00"))
                .description("Transferência PIX de alto valor")
                .initiatedBy("user-123")
                .geoLocalization("-23.5505,-46.6333")
                .initiatedAt(LocalDateTime.of(2024, 1, 1, 10, 0, 0))
                .build();
    }
}