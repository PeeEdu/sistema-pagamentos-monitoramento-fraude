package com.user.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequestedEvent {
    private String userId;
    private String email;
    private String resetToken;
    private LocalDateTime requestedAt;
    private String userName;

    public PasswordResetRequestedEvent(String userId, String email, String resetToken, String userName) {
        this.userId = userId;
        this.email = email;
        this.resetToken = resetToken;
        this.userName = userName;
        this.requestedAt = LocalDateTime.now();
    }
}