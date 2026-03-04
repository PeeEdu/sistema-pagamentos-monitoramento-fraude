package com.transferencia_service.consumer;

import com.transferencia_service.event.UserCreatedEvent;
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
public class UserCreatedConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "user-created-topic",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(
            @Payload UserCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("Mensagem recebida - Partition: {}, Offset: {}, Event: {}",
                partition, offset, event);

        try {
            notificationService.processUserCreatedEvent(event);
        } catch (Exception e) {
            log.error("Erro ao processar evento: {}", event, e);
        }
    }
}
