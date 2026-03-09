package com.bank_account.consumer;

import com.bank_account.event.UserCreatedEvent;
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
public class CreateBankAccountConsumer {

    private final BankAccountService bankAccountService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${kafka.topics.user-created}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUserCreatedEvent(
            @Payload LinkedHashMap<String, Object> payload,  // ✅ Recebe como LinkedHashMap
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("========================================");
        log.info("📥 MENSAGEM RECEBIDA");
        log.info("Partition: {}, Offset: {}", partition, offset);
        log.info("Payload: {}", payload);
        log.info("========================================");

        try {
            // ✅ Converte manualmente para UserCreatedEvent
            UserCreatedEvent event = objectMapper.convertValue(payload, UserCreatedEvent.class);

            log.info("📋 Evento convertido:");
            log.info("   UserId: {}", event.getUserId());
            log.info("   Nome: {}", event.getName());
            log.info("   Email: {}", event.getEmail());
            log.info("   CPF: {}", event.getCpf());
            log.info("   CreatedAt: {}", event.getCreatedAt());

            bankAccountService.createAccountForUser(event);
            log.info("✅ Conta bancária criada com sucesso!");
            log.info("========================================");

        } catch (Exception e) {
            log.error("❌ Erro ao processar evento", e);
            throw new RuntimeException("Erro ao processar evento de criação de usuário", e);
        }
    }
}