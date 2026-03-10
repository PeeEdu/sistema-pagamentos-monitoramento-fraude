package com.bank_account.consumer;

import com.bank_account.event.TransferInitiatedEvent;
import com.bank_account.service.BankAccountService;
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
public class TransferInitiatedConsumer {

    private final ObjectMapper objectMapper;
    private final BankAccountService bankAccountService;

    @KafkaListener(
            topics = "${kafka.topics.transfer-initiated}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeTransferInitiated(
            @Payload LinkedHashMap<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("========================================");
        log.info("🏦 [BANK ACCOUNT] Transferência recebida para validar");
        log.info("Partition: {}, Offset: {}", partition, offset);
        log.info("========================================");

        try {
            TransferInitiatedEvent event = objectMapper.convertValue(payload, TransferInitiatedEvent.class);

            log.info("Transfer ID: {}", event.getTransferId());
            log.info("From Account: {}", event.getFromAccountId());
            log.info("Amount: {}", event.getAmount());


            bankAccountService.process(event);

        } catch (Exception e) {
            log.error("❌ Erro ao processar transferência", e);
            throw new RuntimeException("Erro ao processar transferência", e);
        }
    }
}