package com.bank_account.consumer;

import com.bank_account.event.TransferValidatedEvent;
import com.bank_account.service.TransferProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class TransferValidatedConsumer {

    private final ObjectMapper objectMapper;
    private final TransferProcessingService transferProcessingService;

    @KafkaListener(
            topics = "${kafka.topics.transfer-validated}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(
            @Payload LinkedHashMap<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("========================================");
        log.info("🏦 [BANK ACCOUNT] Validação recebida");
        log.info("Partition: {}, Offset: {}", partition, offset);
        log.info("========================================");

        try {
            TransferValidatedEvent event = objectMapper.convertValue(payload, TransferValidatedEvent.class);

            log.info("Transfer ID: {}", event.getTransferId());
            log.info("Aprovado: {}", event.isApproved());
            log.info("Risk Score: {}", event.getRiskScore());

            // ✅ Decisão simplificada
            if (event.isApproved()) {
                transferProcessingService.process(event);
            } else {
                transferProcessingService.reject(event);
            }

        } catch (Exception e) {
            log.error("❌ Erro ao processar validação", e);
        }
    }
}