package com.transferencia_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendWelcomeEmail(String to, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Bem-vindo ao Banco!");
            message.setText(String.format(
                    "Olá %s,\n\n" +
                            "Seja bem-vindo ao nosso sistema bancário!\n\n" +
                            "Sua conta foi criada com sucesso.\n\n" +
                            "Atenciosamente,\n" +
                            "Equipe Bank Account",
                    name
            ));

            mailSender.send(message);
            log.info("Email enviado com sucesso para: {}", to);

        } catch (Exception e) {
            log.error("Erro ao enviar email para: {}", to, e);
            throw new RuntimeException("Falha ao enviar email", e);
        }
    }
}
