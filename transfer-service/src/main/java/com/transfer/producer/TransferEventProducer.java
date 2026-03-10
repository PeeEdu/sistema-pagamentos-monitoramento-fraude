package com.transfer.producer;

import com.transfer.event.TransferInitiatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferEventProducer {

    @Value("${kafka.topics.transfer-initiated:transfer-initiated-topic}")
    private String topic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendInitiatedTransfer(TransferInitiatedEvent event) {
        log.info("💸 Enviando transferência: {}", event.getTransferId());
        kafkaTemplate.send(topic, event.getTransferId(), event);
    }
}