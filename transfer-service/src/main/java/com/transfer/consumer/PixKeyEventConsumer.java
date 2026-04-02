package com.transfer.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.entity.AccountPixKey;
import com.transfer.event.PixKeyCreatedEvent;
import com.transfer.event.PixKeyDeletedEvent;
import com.transfer.repository.AccountPixKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

// No Transfer Service
@Slf4j
@Component
@RequiredArgsConstructor
public class PixKeyEventConsumer {

    private final AccountPixKeyRepository accountPixKeyRepository;
    private final ObjectMapper objectMapper; // ← Adicionar

    @KafkaListener(topics = "${kafka.topics.pix-key-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void handlePixKeyCreated(LinkedHashMap<String, Object> eventData) { // ← Usar LinkedHashMap
        log.info("📥 Recebido evento PixKeyCreated: {}", eventData);

        try {
            PixKeyCreatedEvent event = objectMapper.convertValue(eventData, PixKeyCreatedEvent.class);

            if (accountPixKeyRepository.existsByPixKey(event.getPixKey())) {
                log.info("⚠️ Chave PIX já existe, ignorando: {}", event.getPixKey());
                return;
            }

            AccountPixKey accountPixKey = AccountPixKey.builder()
                    .userId(event.getUserId())
                    .accountNumber(event.getAccountNumber())
                    .pixKey(event.getPixKey())
                    .pixKeyType(event.getPixKeyType())
                    .build();

            accountPixKeyRepository.save(accountPixKey);
            log.info("✅ Chave PIX sincronizada localmente: {}", event.getPixKey());

        } catch (Exception e) {
            log.error("❌ Erro ao processar PixKeyCreated: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.pix-key-deleted}", groupId = "${spring.kafka.consumer.group-id}")
    public void handlePixKeyDeleted(LinkedHashMap<String, Object> eventData) {
        log.info("📥 Recebido evento PixKeyDeleted: {}", eventData);

        try {
            PixKeyDeletedEvent event = objectMapper.convertValue(eventData, PixKeyDeletedEvent.class);

            accountPixKeyRepository.deleteByPixKey(event.getPixKey());
            log.info("✅ Chave PIX removida localmente: {}", event.getPixKey());

        } catch (Exception e) {
            log.error("❌ Erro ao processar PixKeyDeleted: {}", e.getMessage(), e);
        }
    }
}