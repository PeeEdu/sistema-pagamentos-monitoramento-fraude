package com.transfer.event;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PixKeyDeletedEvent {
    private String userId;
    private String accountNumber;
    private String pixKey;
    private Instant deletedAt;
}