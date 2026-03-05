package com.bank_account.producer;
import com.bank_account.event.CreateBankAccountEvent;
import com.bank_account.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankAccountEventProducer {
    private final String TOPIC = "bank-account-create";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendToCreateBankAccount(CreateBankAccountEvent createBankAccountEvent){
        log.info("Enviando um usuário para abertura de conta");

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(TOPIC, createBankAccountEvent.getUserId(), createBankAccountEvent);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Evento enviado com sucesso para o tópico: {} com offset: {}",
                        TOPIC, result.getRecordMetadata().offset());
            } else {
                log.error("Erro ao enviar evento para o tópico: {}", TOPIC, ex);
            }
        });
    }
}
