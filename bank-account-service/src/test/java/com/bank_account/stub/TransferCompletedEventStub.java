package com.bank_account.stub;

import com.bank_account.event.TransferCompletedEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferCompletedEventStub {

    private TransferCompletedEventStub() {
    }

    public static TransferCompletedEvent buildEvent() {
        return TransferCompletedEvent.builder()
                .transferId("transfer-123")
                .fromAccountNumber("123456")
                .toAccountId("joao.silva@email.com")
                .amount(new BigDecimal("100.00"))
                .status("COMPLETED")
                .description("Transferência PIX")
                .failureReason(null)
                .initiatedBy("user-123")
                .geoLocalization("N/A")
                .completedAt(LocalDateTime.now())
                .build();
    }
}