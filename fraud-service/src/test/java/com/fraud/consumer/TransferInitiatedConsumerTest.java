package com.fraud.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraud.event.TransferInitiatedEvent;
import com.fraud.service.FraudDetectionService;
import com.fraud.stub.TransferInitiatedEventStub;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TransferInitiatedConsumerTest {

    private final FraudDetectionService fraudDetectionService;
    private final ObjectMapper objectMapper;
    private final TransferInitiatedConsumer transferInitiatedConsumer;

    TransferInitiatedConsumerTest() {
        this.fraudDetectionService = mock(FraudDetectionService.class);
        this.objectMapper = mock(ObjectMapper.class);
        this.transferInitiatedConsumer = new TransferInitiatedConsumer(fraudDetectionService, objectMapper);
    }

    @Test
    void consume_DeveAnalisarTransferencia_QuandoEventoForValido() {
        LinkedHashMap<String, Object> payload = new LinkedHashMap<>();
        TransferInitiatedEvent event = TransferInitiatedEventStub.buildEvent();

        when(objectMapper.convertValue(payload, TransferInitiatedEvent.class)).thenReturn(event);

        assertDoesNotThrow(() ->
                transferInitiatedConsumer.consume(payload, 1, 10L, "transfer-initiated-topic")
        );

        verify(objectMapper).convertValue(payload, TransferInitiatedEvent.class);
        verify(fraudDetectionService).analyze(event);
    }

    @Test
    void consume_DeveLancarRuntimeException_QuandoOcorrerErroAoProcessarEvento() {
        LinkedHashMap<String, Object> payload = new LinkedHashMap<>();

        when(objectMapper.convertValue(payload, TransferInitiatedEvent.class))
                .thenThrow(new RuntimeException("Erro ao converter payload"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> transferInitiatedConsumer.consume(payload, 1, 10L, "transfer-initiated-topic")
        );

        assertEquals("Erro ao processar análise de fraude", exception.getMessage());
        assertEquals("Erro ao converter payload", exception.getCause().getMessage());

        verify(objectMapper).convertValue(payload, TransferInitiatedEvent.class);
        verify(fraudDetectionService, never()).analyze(any());
    }
}