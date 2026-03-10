package com.transfer.dto.response;

import com.transfer.enums.PixKeyType;
import com.transfer.enums.TransferStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record PixResponse(
        String id,
        String fromAccountId,
        String pixKey,
        PixKeyType pixKeyType,
        BigDecimal amount,
        String description,
        TransferStatus status,
        String failureReason,
        Instant createdAt,
        Instant updatedAt,
        String initiatedBy
) {
}