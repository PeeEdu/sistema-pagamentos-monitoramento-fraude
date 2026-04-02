package com.bank_account.producer;

import com.bank_account.event.TransferCompletedEvent;
import com.bank_account.stub.TransferCompletedEventStub;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class TransferCompletedProducerTest {

    private KafkaTemplate<String, Object> kafkaTemplate;
    private TransferCompletedProducer transferCompletedProducer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        transferCompletedProducer = new TransferCompletedProducer(kafkaTemplate);
        ReflectionTestUtils.setField(transferCompletedProducer, "topic", "transfer-completed-topic");
    }

    @Test
    void sendTransferCompleted_DeveEnviarEventoParaTopicoCorreto_QuandoEventoForValido() {
        TransferCompletedEvent event = TransferCompletedEventStub.buildEvent();

        @SuppressWarnings("unchecked")
        SendResult<String, Object> sendResult = mock(SendResult.class);
        RecordMetadata recordMetadata = mock(RecordMetadata.class);

        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        when(recordMetadata.offset()).thenReturn(10L);

        CompletableFuture<SendResult<String, Object>> future =
                CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send("transfer-completed-topic", event.getTransferId(), event))
                .thenReturn(future);

        assertDoesNotThrow(() -> transferCompletedProducer.sendTransferCompleted(event));

        verify(kafkaTemplate).send("transfer-completed-topic", event.getTransferId(), event);
    }

    @Test
    void sendTransferCompleted_DeveExecutarSemLancarExcecao_QuandoEnvioForBemSucedido() {
        TransferCompletedEvent event = TransferCompletedEventStub.buildEvent();

        @SuppressWarnings("unchecked")
        SendResult<String, Object> sendResult = mock(SendResult.class);
        RecordMetadata recordMetadata = mock(RecordMetadata.class);

        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        when(recordMetadata.offset()).thenReturn(10L);

        CompletableFuture<SendResult<String, Object>> future =
                CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send("transfer-completed-topic", event.getTransferId(), event))
                .thenReturn(future);

        assertDoesNotThrow(() -> transferCompletedProducer.sendTransferCompleted(event));

        verify(kafkaTemplate).send("transfer-completed-topic", event.getTransferId(), event);
    }

    @Test
    void sendTransferCompleted_DeveExecutarSemLancarExcecao_QuandoEnvioFalhar() {
        TransferCompletedEvent event = TransferCompletedEventStub.buildEvent();

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Erro ao enviar evento"));

        when(kafkaTemplate.send("transfer-completed-topic", event.getTransferId(), event))
                .thenReturn(future);

        assertDoesNotThrow(() -> transferCompletedProducer.sendTransferCompleted(event));

        verify(kafkaTemplate).send("transfer-completed-topic", event.getTransferId(), event);
    }
}