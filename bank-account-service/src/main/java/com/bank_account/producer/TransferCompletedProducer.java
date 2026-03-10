package com.bank_account.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferCompletedProducer {

    @Value("${kafka.topics.transfer-completed}")
    private String topic;

    private final KafkaTemplate<String, Object> kafkaTemplate;
//TransferCompletedEvent event
    public void sendTransferCompleted() {
        log.info("📤 Enviando resultado: - Status: ");
//                event.getTransferId(), event.getStatus());
//
//        kafkaTemplate.send(topic, event.getTransferId(), event)
//                .whenComplete((result, ex) -> {
//                    if (ex == null) {
//                        log.info("✅ Evento enviado - Offset: {}",
//                                result.getRecordMetadata().offset());
//                    } else {
//                        log.error("❌ Erro ao enviar: {}", ex.getMessage());
//                    }
//                });
    }
}