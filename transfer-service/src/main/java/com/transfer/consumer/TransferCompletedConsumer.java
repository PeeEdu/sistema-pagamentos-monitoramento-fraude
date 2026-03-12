package com.transfer.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.event.TransferCompletedEvent;
import com.transfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
@Slf4j
@Component
@RequiredArgsConstructor
public class TransferCompletedConsumer {
    private final TransferService transferService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${kafka.topics.transfer-completed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUserCreatedEvent(
            @Payload LinkedHashMap<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("MENSAGEM RECEBIDA");
        log.info("Partition: {}, Offset: {}", partition, offset);
        log.info("Payload: {}", payload);

        try {
            TransferCompletedEvent event = objectMapper.convertValue(payload, TransferCompletedEvent.class);
            log.info("   transferId: {}", event.getTransferId());

            transferService.updateStatus(event);
            log.info("Transferência concluída, status: {}", event.getStatus());

        } catch (Exception e) {
            log.error("Erro ao processar evento", e);
            throw new RuntimeException("Erro ao processar evento de transferência concluída", e);
        }
    }
}