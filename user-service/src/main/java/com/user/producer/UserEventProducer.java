package com.user.producer;

import com.user.event.UserCreatedEvent;
import com.user.event.PasswordResetRequestedEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventProducer {

    @Value("${kafka.topics.user-created:user-created-topic}")
    private String userCreatedTopic;

    @Value("${kafka.topics.password-reset:password-reset-topic}")
    private String passwordResetTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostConstruct
    public void init() {
        log.info("🔍 DEBUG: userCreatedTopic = {}", userCreatedTopic);
        log.info("🔍 DEBUG: passwordResetTopic = {}", passwordResetTopic);
    }

    public void sendUserCreatedEvent(UserCreatedEvent event) {
        log.info("📤 Enviando evento de usuário criado: {}", event);

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(userCreatedTopic, event.getUserId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("✅ Evento enviado com sucesso para o tópico: {} com offset: {}",
                        userCreatedTopic, result.getRecordMetadata().offset());
            } else {
                log.error("❌ Erro ao enviar evento para o tópico: {}", userCreatedTopic, ex);
            }
        });
    }

    public void sendPasswordResetEvent(PasswordResetRequestedEvent event) {
        log.info("📤 Enviando evento de reset de senha: {}", event);
        log.info("🔍 DEBUG: Tipo do evento: {}", event.getClass().getSimpleName());
        log.info("🔍 DEBUG: Tópico de destino: {}", passwordResetTopic);
        log.info("🔍 DEBUG: Valor da propriedade passwordResetTopic: {}", passwordResetTopic);

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(passwordResetTopic, event.getUserId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("✅ Evento de reset enviado com sucesso para o tópico: {} com offset: {}",
                        passwordResetTopic, result.getRecordMetadata().offset());
            } else {
                log.error("❌ Erro ao enviar evento de reset para o tópico: {}", passwordResetTopic, ex);
            }
        });
    }
}