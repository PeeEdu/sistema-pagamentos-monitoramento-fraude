package com.bank_account.stub;

import com.bank_account.event.TransferValidatedEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TransferValidatedEventStub {

    private TransferValidatedEventStub() {
    }

    public static TransferValidatedEvent buildApprovedEvent() {
        return TransferValidatedEvent.builder()
                .transferId("transfer-123")
                .fromAccountNumber("123456")
                .pixKey("joao.silva@email.com")
                .amount(new BigDecimal("100.00"))
                .description("Transferência PIX")
                .initiatedBy("user-123")
                .approved(true)
                .riskScore(new BigDecimal("10.50"))
                .fraudTypes(List.of())
                .rejectionReason(null)
                .validatedAt(LocalDateTime.of(2024, 1, 1, 10, 0, 0))
                .build();
    }

    public static TransferValidatedEvent buildRejectedEvent() {
        return TransferValidatedEvent.builder()
                .transferId("transfer-123")
                .fromAccountNumber("123456")
                .pixKey("joao.silva@email.com")
                .amount(new BigDecimal("100.00"))
                .description("Transferência PIX")
                .initiatedBy("user-123")
                .approved(false)
                .riskScore(new BigDecimal("95.00"))
                .fraudTypes(List.of())
                .rejectionReason("Transferência rejeitada por fraude")
                .validatedAt(LocalDateTime.of(2024, 1, 1, 10, 0, 0))
                .build();
    }
}