package com.transferencia_service.event;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {
    private String userId;
    private String name;
    private String email;
    private String cpf;
    private LocalDateTime createdAt;
}