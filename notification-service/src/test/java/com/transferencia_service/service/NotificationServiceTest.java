package com.transferencia_service.service;

import com.transferencia_service.entity.NotificationLog;
import com.transferencia_service.event.PasswordResetRequestedEvent;
import com.transferencia_service.event.UserCreatedEvent;
import com.transferencia_service.repository.NotificationLogRepository;
import com.transferencia_service.stub.PasswordResetRequestedEventStub;
import com.transferencia_service.stub.UserCreatedEventStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private final EmailService emailService;
    private final NotificationLogRepository notificationLogRepository;
    private final NotificationService notificationService;

    NotificationServiceTest() {
        this.emailService = mock(EmailService.class);
        this.notificationLogRepository = mock(NotificationLogRepository.class);
        this.notificationService = new NotificationService(emailService, notificationLogRepository);
    }

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificationService, "frontendUrl", "http://localhost:3000");
    }

    @Test
    void processUserCreatedEvent_DeveSalvarLogComStatusSuccess_QuandoEmailForEnviadoComSucesso() {
        UserCreatedEvent event = UserCreatedEventStub.buildEvent();

        assertDoesNotThrow(() -> notificationService.processUserCreatedEvent(event));

        verify(emailService).sendWelcomeEmail(event.getEmail(), event.getName());
        verify(notificationLogRepository).save(argThat(log ->
                log.getUserId().equals(event.getUserId()) &&
                        log.getEmail().equals(event.getEmail()) &&
                        log.getType().equals("EMAIL") &&
                        log.getStatus().equals("SUCCESS") &&
                        log.getMessage().equals("Email de boas-vindas") &&
                        log.getSentAt() != null &&
                        log.getErrorMessage() == null
        ));
    }

    @Test
    void processUserCreatedEvent_DeveSalvarLogComStatusFailed_QuandoEmailServiceLancarExcecao() {
        UserCreatedEvent event = UserCreatedEventStub.buildEvent();

        doThrow(new RuntimeException("Erro ao enviar email"))
                .when(emailService)
                .sendWelcomeEmail(event.getEmail(), event.getName());

        assertDoesNotThrow(() -> notificationService.processUserCreatedEvent(event));

        verify(emailService).sendWelcomeEmail(event.getEmail(), event.getName());
        verify(notificationLogRepository).save(argThat(log ->
                log.getUserId().equals(event.getUserId()) &&
                        log.getEmail().equals(event.getEmail()) &&
                        log.getType().equals("EMAIL") &&
                        log.getStatus().equals("FAILED") &&
                        log.getMessage().equals("Email de boas-vindas") &&
                        log.getSentAt() == null &&
                        "Erro ao enviar email".equals(log.getErrorMessage())
        ));
    }

    @Test
    void processUserCreatedEvent_DeveSalvarLogMesmoQuandoOcorrerErroNoEnvio() {
        UserCreatedEvent event = UserCreatedEventStub.buildEvent();

        doThrow(new RuntimeException("Erro ao enviar email"))
                .when(emailService)
                .sendWelcomeEmail(event.getEmail(), event.getName());

        notificationService.processUserCreatedEvent(event);

        verify(notificationLogRepository).save(any(NotificationLog.class));
    }

    @Test
    void processPasswordResetEvent_DeveSalvarLogComStatusSuccess_QuandoEmailForEnviadoComUserNameInformado() {
        PasswordResetRequestedEvent event = PasswordResetRequestedEventStub.buildEvent();

        assertDoesNotThrow(() -> notificationService.processPasswordResetEvent(event));

        verify(emailService).sendPasswordResetEmail(
                event.getEmail(),
                event.getUserName(),
                "http://localhost:3000/reset-password?token=" + event.getResetToken()
        );

        verify(notificationLogRepository).save(argThat(log ->
                log.getUserId().equals(event.getUserId()) &&
                        log.getEmail().equals(event.getEmail()) &&
                        log.getType().equals("EMAIL") &&
                        log.getStatus().equals("SUCCESS") &&
                        log.getMessage().equals("Email de reset") &&
                        log.getSentAt() != null &&
                        log.getErrorMessage() == null
        ));
    }

    @Test
    void processPasswordResetEvent_DeveUsarUsuarioPadrao_QuandoUserNameForNulo() {
        PasswordResetRequestedEvent event = PasswordResetRequestedEventStub.buildEventWithoutUserName();

        assertDoesNotThrow(() -> notificationService.processPasswordResetEvent(event));

        verify(emailService).sendPasswordResetEmail(
                event.getEmail(),
                "Usuário",
                "http://localhost:3000/reset-password?token=" + event.getResetToken()
        );

        verify(notificationLogRepository).save(argThat(log ->
                log.getStatus().equals("SUCCESS") &&
                        log.getSentAt() != null
        ));
    }

    @Test
    void processPasswordResetEvent_DeveSalvarLogComStatusFailed_QuandoEmailServiceLancarExcecao() {
        PasswordResetRequestedEvent event = PasswordResetRequestedEventStub.buildEvent();

        doThrow(new RuntimeException("Erro ao enviar reset"))
                .when(emailService)
                .sendPasswordResetEmail(
                        eq(event.getEmail()),
                        eq(event.getUserName()),
                        eq("http://localhost:3000/reset-password?token=" + event.getResetToken())
                );

        assertDoesNotThrow(() -> notificationService.processPasswordResetEvent(event));

        verify(emailService).sendPasswordResetEmail(
                event.getEmail(),
                event.getUserName(),
                "http://localhost:3000/reset-password?token=" + event.getResetToken()
        );

        verify(notificationLogRepository).save(argThat(log ->
                log.getUserId().equals(event.getUserId()) &&
                        log.getEmail().equals(event.getEmail()) &&
                        log.getType().equals("EMAIL") &&
                        log.getStatus().equals("FAILED") &&
                        log.getMessage().equals("Email de reset") &&
                        log.getSentAt() == null &&
                        "Erro ao enviar reset".equals(log.getErrorMessage())
        ));
    }

    @Test
    void processPasswordResetEvent_DeveSalvarLogMesmoQuandoOcorrerErroNoEnvio() {
        PasswordResetRequestedEvent event = PasswordResetRequestedEventStub.buildEvent();

        doThrow(new RuntimeException("Erro ao enviar reset"))
                .when(emailService)
                .sendPasswordResetEmail(anyString(), anyString(), anyString());

        notificationService.processPasswordResetEvent(event);

        verify(notificationLogRepository).save(any(NotificationLog.class));
    }
}