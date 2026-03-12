package com.fraud.producer;

import com.fraud.event.TransferValidatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferValidatedProducer {

    @Value("${kafka.topics.transfer-validated}")
    private String topic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(TransferValidatedEvent event) {
        log.info("📤 Enviando validação: {} - Aprovado: {}",
                event.getTransferId(), event.isApproved());

        kafkaTemplate.send(topic, event.getTransferId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("✅ Validação enviada - Offset: {}",
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("❌ Erro ao enviar validação: {}", ex.getMessage());
                    }
                });
    }
}