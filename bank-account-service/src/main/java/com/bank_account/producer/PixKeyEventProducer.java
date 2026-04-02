
package com.bank_account.producer;

import com.bank_account.entity.BankAccount;
import com.bank_account.entity.PixKey;
import com.bank_account.event.PixKeyCreatedEvent;
import com.bank_account.event.PixKeyDeletedEvent;
import com.bank_account.mapper.BankAccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PixKeyEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final BankAccountMapper bankAccountMapper;

    @Value("${kafka.topics.pix-key-created}")
    private String pixKeyCreatedTopic;

    @Value("${kafka.topics.pix-key-deleted}")
    private String pixKeyDeletedTopic;

    public void sendPixKeyCreatedEvent(BankAccount bankAccount, PixKey pixKey) {
        try {
            PixKeyCreatedEvent event = bankAccountMapper.toPixKeyCreatedEvent(bankAccount, pixKey);
            kafkaTemplate.send(pixKeyCreatedTopic, event);
            log.info("📤 Evento PixKeyCreated enviado para tópico {}: {}", pixKeyCreatedTopic, event);
        } catch (Exception e) {
            log.error("❌ Erro ao enviar PixKeyCreatedEvent: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void sendPixKeyDeletedEvent(BankAccount bankAccount, String pixKey) {
        try {
            PixKeyDeletedEvent event = bankAccountMapper.toPixKeyDeletedEvent(bankAccount, pixKey);

            kafkaTemplate.send(pixKeyDeletedTopic, event);
            log.info("📤 Evento PixKeyDeleted enviado para tópico {}: {}", pixKeyDeletedTopic, event);
        } catch (Exception e) {
            log.error("❌ Erro ao enviar PixKeyDeletedEvent: {}", e.getMessage(), e);
            throw e;
        }
    }
}