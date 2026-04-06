package com.transferencia_service.consumer;

import com.transferencia_service.event.PasswordResetRequestedEvent;
import com.transferencia_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordResetConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${kafka.topics.password-reset}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "passwordResetKafkaListenerContainerFactory"
    )
    public void consume(
            @Payload PasswordResetRequestedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic
    ) {
        log.info("📨 Mensagem de reset recebida do tópico: {} - Partition: {}, Offset: {}",
                topic, partition, offset);
        log.info("🔐 Evento: {}", event);

        try {
            notificationService.processPasswordResetEvent(event);
            log.info("✅ Email de reset processado com sucesso para: {}", event.getEmail());
        } catch (Exception e) {
            log.error("❌ Erro ao processar evento de reset: {}", event, e);
            throw e;
        }
    }
}