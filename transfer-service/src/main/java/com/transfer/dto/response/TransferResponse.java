package com.transfer.dto.response;

import com.transfer.enums.TransferStatus;
import com.transfer.enums.TransferType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record TransferResponse(
        String id,
        String fromAccountNumber,
        String toAccountId,
        BigDecimal amount,
        TransferStatus status,
        TransferType type,
        String description,
        String failureReason,
        Instant createdAt,
        Instant updatedAt
) {
}