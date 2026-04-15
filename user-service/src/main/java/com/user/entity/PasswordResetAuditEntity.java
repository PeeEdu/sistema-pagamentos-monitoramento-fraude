package com.user.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "password_reset_audit")
public class PasswordResetAuditEntity {

    @Id
    private String id;

    private String userId;
    private String email;

    private String token;
    private String status;

    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;
    private LocalDateTime expiresAt;

    private String failureReason;

    private LocalDateTime passwordChangedAt;
}