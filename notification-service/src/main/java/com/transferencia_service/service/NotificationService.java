package com.transferencia_service.service;



import com.transferencia_service.entity.NotificationLog;
import com.transferencia_service.event.UserCreatedEvent;
import com.transferencia_service.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;
    private final NotificationLogRepository notificationLogRepository;

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
            // Envia o email
            emailService.sendWelcomeEmail(event.getEmail(), event.getName());

            // Atualiza o log como sucesso
            notificationLog.setStatus("SUCCESS");
            notificationLog.setSentAt(LocalDateTime.now());

            log.info("Notificação enviada com sucesso para: {}", event.getEmail());

        } catch (Exception e) {
            log.error("Erro ao enviar notificação para: {}", event.getEmail(), e);

            // Atualiza o log como falha
            notificationLog.setStatus("FAILED");
            notificationLog.setErrorMessage(e.getMessage());
        } finally {
            // Salva o log no MongoDB
            notificationLogRepository.save(notificationLog);
        }
    }
}
