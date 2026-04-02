package com.transferencia_service.service;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;

import java.util.Map;
import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;

    EmailServiceTest() {
        this.mailSender = mock(JavaMailSender.class);
        this.templateEngine = mock(TemplateEngine.class);
        this.emailService = new EmailService(mailSender, templateEngine);
    }

    @Test
    void sendTemplateEmail_DeveEnviarEmailComSucesso_QuandoDadosForemValidos() {
        MimeMessage mimeMessage = new MimeMessage((Session) null);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("welcome-email"), any()))
                .thenReturn("<html><body>Bem-vindo</body></html>");

        assertDoesNotThrow(() ->
                emailService.sendTemplateEmail(
                        "joao.silva@email.com",
                        "Assunto teste",
                        "welcome-email",
                        Map.of("name", "João Silva")
                )
        );

        verify(mailSender).createMimeMessage();
        verify(templateEngine).process(eq("welcome-email"), any());
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendTemplateEmail_DeveEnviarEmailComSucesso_QuandoVariaveisForemNulas() {
        MimeMessage mimeMessage = new MimeMessage((Session) null);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("welcome-email"), any()))
                .thenReturn("<html><body>Bem-vindo</body></html>");

        assertDoesNotThrow(() ->
                emailService.sendTemplateEmail(
                        "joao.silva@email.com",
                        "Assunto teste",
                        "welcome-email",
                        null
                )
        );

        verify(mailSender).createMimeMessage();
        verify(templateEngine).process(eq("welcome-email"), any());
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendTemplateEmail_DeveLancarRuntimeException_QuandoOcorrerErroAoEnviarEmail() {
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Erro ao criar mensagem"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> emailService.sendTemplateEmail(
                        "joao.silva@email.com",
                        "Assunto teste",
                        "welcome-email",
                        Map.of("name", "João Silva")
                )
        );

        assertEquals("Falha ao enviar email", exception.getMessage());
        verify(mailSender).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendWelcomeEmail_DeveChamarSendTemplateEmailComParametrosCorretos() {
        EmailService spyService = spy(new EmailService(mailSender, templateEngine));

        doNothing().when(spyService).sendTemplateEmail(anyString(), anyString(), anyString(), anyMap());

        spyService.sendWelcomeEmail("joao.silva@email.com", "João Silva");

        verify(spyService).sendTemplateEmail(
                "joao.silva@email.com",
                "🎉 Bem-vindo ao NexBank!",
                "welcome-email",
                Map.of("name", "João Silva")
        );
    }

    @Test
    void sendTransactionEmail_DeveChamarSendTemplateEmailComParametrosCorretos() {
        EmailService spyService = spy(new EmailService(mailSender, templateEngine));

        doNothing().when(spyService).sendTemplateEmail(anyString(), anyString(), anyString(), anyMap());

        spyService.sendTransactionEmail(
                "joao.silva@email.com",
                "João Silva",
                "R$ 100,00",
                "Maria Souza"
        );

        verify(spyService).sendTemplateEmail(
                "joao.silva@email.com",
                "💳 Transação realizada - NexBank",
                "transaction-email",
                Map.of(
                        "name", "João Silva",
                        "amount", "R$ 100,00",
                        "recipient", "Maria Souza"
                )
        );
    }

    @Test
    void sendPasswordResetEmail_DeveChamarSendTemplateEmailComParametrosCorretos() {
        EmailService spyService = spy(new EmailService(mailSender, templateEngine));

        doNothing().when(spyService).sendTemplateEmail(anyString(), anyString(), anyString(), anyMap());

        spyService.sendPasswordResetEmail(
                "joao.silva@email.com",
                "João Silva",
                "https://nexbank.com/reset"
        );

        verify(spyService).sendTemplateEmail(
                "joao.silva@email.com",
                "🔒 Redefinição de senha - NexBank",
                "password-reset-email",
                Map.of(
                        "name", "João Silva",
                        "resetLink", "https://nexbank.com/reset"
                )
        );
    }
}