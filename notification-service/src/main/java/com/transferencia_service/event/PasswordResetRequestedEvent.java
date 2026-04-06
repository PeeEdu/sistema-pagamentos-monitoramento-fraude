package com.transferencia_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetRequestedEvent {
    private String userId;
    private String email;
    private String resetToken;
    private LocalDateTime requestedAt;
    private String userName;
}