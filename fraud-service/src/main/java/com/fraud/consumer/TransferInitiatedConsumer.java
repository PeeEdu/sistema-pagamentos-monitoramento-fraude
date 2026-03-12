package com.fraud.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraud.event.TransferInitiatedEvent;
import com.fraud.service.FraudDetectionService;
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
public class TransferInitiatedConsumer {

    private final FraudDetectionService fraudDetectionService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${kafka.topics.transfer-initiated}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(
            @Payload LinkedHashMap<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic
    ) {

        log.info("🚨 [FRAUD] Transferência recebida para análise");
        log.info("Tópico: {}", topic);
        log.info("Partition: {}", partition);
        log.info("Offset: {}", offset);
        log.info("Payload: {}", payload);
        log.info("");
        try {
            //Converte payload para TransferInitiatedEvent
            TransferInitiatedEvent event = objectMapper.convertValue(payload, TransferInitiatedEvent.class);

            log.info("📋 Evento convertido:");
            log.info("   Transfer ID: {}", event.getTransferId());
            log.info("   From Account: {}", event.getFromAccountNumber());
            log.info("   Pix Key: {}", event.getPixKey());
            log.info("   Amount: {}", event.getAmount());
            log.info("   Initiated By: {}", event.getInitiatedBy());

            fraudDetectionService.analyze(event);

        } catch (Exception e) {
            log.error("❌ Erro ao processar transferência", e);
            throw new RuntimeException("Erro ao processar análise de fraude", e);
        }
    }
}