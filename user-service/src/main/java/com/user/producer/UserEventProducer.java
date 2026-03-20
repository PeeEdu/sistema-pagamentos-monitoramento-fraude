package com.user.producer;

import com.user.event.UserCreatedEvent;
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
    private String topic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendUserCreatedEvent(UserCreatedEvent event) {
        log.info("📤 Enviando evento de usuário criado: {}", event);

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, event.getUserId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("✅ Evento enviado com sucesso para o tópico: {} com offset: {}",
                        topic, result.getRecordMetadata().offset());
            } else {
                log.error("❌ Erro ao enviar evento para o tópico: {}", topic, ex);
            }
        });
    }
}