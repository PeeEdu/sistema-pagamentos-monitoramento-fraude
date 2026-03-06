package com.bank_account.consumer;

import com.bank_account.event.UserCreatedEvent;
import com.bank_account.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateBankAccountConsumer {

    private final BankAccountService bankAccountService;

    @KafkaListener(
            topics = "${kafka.topics.user-created}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUserCreatedEvent(
            @Payload UserCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("📥 [BANK ACCOUNT SERVICE] Recebido evento - Partition: {}, Offset: {}",
                partition, offset);
        log.info("📋 Evento: {}", event);

        try {
            bankAccountService.createAccountForUser(event);
            log.info("✅ Conta bancária criada para o usuário: {}", event.getUserId());
        } catch (Exception e) {
            log.error("❌ Erro ao criar conta bancária para usuário: {}", event.getUserId(), e);
            // TODO: Implementar retry ou dead letter queue
        }
    }
}