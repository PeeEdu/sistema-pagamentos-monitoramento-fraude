package com.transferencia_service.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("no-reply@nexbank.local", "NexBank");
            helper.setTo(to);
            helper.setSubject(subject);

            Context context = new Context();
            if (variables != null) {
                variables.forEach(context::setVariable);
            }

            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Email '{}' enviado com sucesso para: {}", subject, to);

        } catch (Exception e) {
            log.error("Erro ao enviar email '{}' para: {}", subject, to, e);
            throw new RuntimeException("Falha ao enviar email", e);
        }
    }

    public void sendWelcomeEmail(String to, String name) {
        sendTemplateEmail(
                to,
                "🎉 Bem-vindo ao NexBank!",
                "welcome-email",
                Map.of("name", name)
        );
    }

    public void sendTransactionEmail(String to, String name, String amount, String recipient) {
        sendTemplateEmail(
                to,
                "💳 Transação realizada - NexBank",
                "transaction-email",
                Map.of(
                        "name", name,
                        "amount", amount,
                        "recipient", recipient
                )
        );
    }

    public void sendPasswordResetEmail(String to, String name, String resetLink) {
        sendTemplateEmail(
                to,
                "🔒 Redefinição de senha - NexBank",
                "password-reset-email",
                Map.of(
                        "name", name,
                        "resetLink", resetLink
                )
        );
    }
}