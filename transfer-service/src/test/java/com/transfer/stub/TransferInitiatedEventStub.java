package com.transfer.stub;

import com.transfer.event.TransferInitiatedEvent;
import com.transfer.enums.PixKeyType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferInitiatedEventStub {

    private TransferInitiatedEventStub() {
    }

    public static TransferInitiatedEvent buildEvent() {
        return TransferInitiatedEvent.builder()
                .transferId("transfer-123")
                .fromAccountNumber("123456")
                .pixKey("joao@email.com")
                .pixKeyType(PixKeyType.EMAIL)
                .amount(new BigDecimal("100.00"))
                .description("Transferência PIX")
                .initiatedBy("user-123")
                .geoLocalization("-23.5505,-46.6333")
                .initiatedAt(LocalDateTime.now())
                .build();
    }
}