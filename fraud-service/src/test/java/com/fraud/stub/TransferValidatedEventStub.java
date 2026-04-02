package com.fraud.stub;

import com.fraud.enums.FraudType;
import com.fraud.event.TransferValidatedEvent;

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
                .amount(new BigDecimal("1000.00"))
                .description("Transferência PIX")
                .initiatedBy("user-123")
                .approved(true)
                .riskScore(10.0)
                .fraudTypes(List.of())
                .rejectionReason(null)
                .validatedAt(LocalDateTime.of(2024, 1, 1, 10, 5, 0))
                .build();
    }

    public static TransferValidatedEvent buildRejectedEvent() {
        return TransferValidatedEvent.builder()
                .transferId("transfer-123")
                .fromAccountNumber("123456")
                .pixKey("joao.silva@email.com")
                .amount(new BigDecimal("1000.00"))
                .description("Transferência PIX")
                .initiatedBy("user-123")
                .approved(false)
                .riskScore(95.0)
                .fraudTypes(List.of(FraudType.HIGH_FREQUENCY, FraudType.HIGH_VALUE))
                .rejectionReason("Transação suspeita")
                .validatedAt(LocalDateTime.of(2024, 1, 1, 10, 5, 0))
                .build();
    }
}