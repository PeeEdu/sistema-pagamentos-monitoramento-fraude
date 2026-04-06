package com.transferencia_service.service;



import com.transferencia_service.entity.NotificationLog;
import com.transferencia_service.event.PasswordResetRequestedEvent;
import com.transferencia_service.event.UserCreatedEvent;
import com.transferencia_service.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;
    private final NotificationLogRepository notificationLogRepository;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public void processUserCreatedEvent(UserCreatedEvent event) {
        log.info("Processando evento de usuário criado: {}", event);

        NotificationLog notificationLog = NotificationLog.builder()
                .userId(event.getUserId())
                .email(event.getEmail())
                .type("EMAIL")
                .status("PENDING")
                .message("Email de boas-vindas")
                .build();
        try {
            emailService.sendWelcomeEmail(event.getEmail(), event.getName());
            notificationLog.setStatus("SUCCESS");
            notificationLog.setSentAt(LocalDateTime.now());

            log.info("Notificação enviada com sucesso para: {}", event.getEmail());

        } catch (Exception e) {

            notificationLog.setStatus("FAILED");
            notificationLog.setErrorMessage(e.getMessage());
        } finally {
            notificationLogRepository.save(notificationLog);
        }
    }

    public void processPasswordResetEvent(PasswordResetRequestedEvent event) {
        log.info("🔄 Processando evento de reset de senha: {}", event.getEmail());

        NotificationLog notificationLog = NotificationLog.builder()
                .userId(event.getUserId())
                .email(event.getEmail())
                .type("EMAIL")
                .status("PENDING")
                .message("Email de reset")
                .build();
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + event.getResetToken();
            String userName = event.getUserName() != null ? event.getUserName() : "Usuário";

            emailService.sendPasswordResetEmail(event.getEmail(), userName, resetLink);
            log.info("✅ Email de reset de senha enviado para: {}", event.getEmail());
            notificationLog.setStatus("SUCCESS");
            notificationLog.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            log.error("❌ Erro ao enviar email de reset de senha para: {}", event.getEmail(), e);
            notificationLog.setStatus("FAILED");
            notificationLog.setErrorMessage(e.getMessage());
            log.error("Erro ao enviar notificação para: {}", event.getEmail(), e);
        }
        finally {
            notificationLogRepository.save(notificationLog);
        }
    }
}
