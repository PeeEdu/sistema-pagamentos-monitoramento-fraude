package com.fraud.producer;

import com.fraud.event.TransferValidatedEvent;
import com.fraud.stub.TransferValidatedEventStub;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class TransferValidatedProducerTest {

    private KafkaTemplate<String, Object> kafkaTemplate;
    private TransferValidatedProducer transferValidatedProducer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        transferValidatedProducer = new TransferValidatedProducer(kafkaTemplate);
        ReflectionTestUtils.setField(transferValidatedProducer, "topic", "transfer-validated-topic");
    }

    @Test
    void send_DeveEnviarEventoParaTopicoCorreto_QuandoEventoForValido() {
        TransferValidatedEvent event = TransferValidatedEventStub.buildApprovedEvent();

        @SuppressWarnings("unchecked")
        SendResult<String, Object> sendResult = mock(SendResult.class);
        RecordMetadata recordMetadata = mock(RecordMetadata.class);

        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        when(recordMetadata.offset()).thenReturn(10L);

        CompletableFuture<SendResult<String, Object>> future =
                CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send("transfer-validated-topic", event.getTransferId(), event))
                .thenReturn(future);

        assertDoesNotThrow(() -> transferValidatedProducer.send(event));

        verify(kafkaTemplate).send("transfer-validated-topic", event.getTransferId(), event);
    }

    @Test
    void send_DeveExecutarSemLancarExcecao_QuandoEnvioForBemSucedido() {
        TransferValidatedEvent event = TransferValidatedEventStub.buildApprovedEvent();

        @SuppressWarnings("unchecked")
        SendResult<String, Object> sendResult = mock(SendResult.class);
        RecordMetadata recordMetadata = mock(RecordMetadata.class);

        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        when(recordMetadata.offset()).thenReturn(10L);

        CompletableFuture<SendResult<String, Object>> future =
                CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send("transfer-validated-topic", event.getTransferId(), event))
                .thenReturn(future);

        assertDoesNotThrow(() -> transferValidatedProducer.send(event));

        verify(kafkaTemplate).send("transfer-validated-topic", event.getTransferId(), event);
    }

    @Test
    void send_DeveExecutarSemLancarExcecao_QuandoEnvioFalhar() {
        TransferValidatedEvent event = TransferValidatedEventStub.buildRejectedEvent();

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Erro ao enviar validação"));

        when(kafkaTemplate.send("transfer-validated-topic", event.getTransferId(), event))
                .thenReturn(future);

        assertDoesNotThrow(() -> transferValidatedProducer.send(event));

        verify(kafkaTemplate).send("transfer-validated-topic", event.getTransferId(), event);
    }
}