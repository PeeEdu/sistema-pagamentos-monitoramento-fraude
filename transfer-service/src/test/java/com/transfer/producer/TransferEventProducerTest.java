package com.transfer.producer;

import com.transfer.event.TransferInitiatedEvent;
import com.transfer.stub.TransferInitiatedEventStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class TransferEventProducerTest {

    private KafkaTemplate<String, Object> kafkaTemplate;
    private TransferEventProducer transferEventProducer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        transferEventProducer = new TransferEventProducer(kafkaTemplate);
        ReflectionTestUtils.setField(transferEventProducer, "topic", "transfer-initiated-topic");
    }

    @Test
    void sendInitiatedTransfer_DeveEnviarEventoParaTopicoCorreto_QuandoEventoForValido() {
        TransferInitiatedEvent event = TransferInitiatedEventStub.buildEvent();

        assertDoesNotThrow(() -> transferEventProducer.sendInitiatedTransfer(event));

        verify(kafkaTemplate).send("transfer-initiated-topic", event.getTransferId(), event);
    }
}