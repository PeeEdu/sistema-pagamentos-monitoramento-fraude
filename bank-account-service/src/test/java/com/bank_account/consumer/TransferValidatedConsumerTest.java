package com.bank_account.consumer;

import com.bank_account.event.TransferValidatedEvent;
import com.bank_account.service.TransferProcessingService;
import com.bank_account.stub.TransferValidatedEventStub;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class TransferValidatedConsumerTest {

    private final ObjectMapper objectMapper;
    private final TransferProcessingService transferProcessingService;
    private final TransferValidatedConsumer transferValidatedConsumer;

    TransferValidatedConsumerTest() {
        this.objectMapper = mock(ObjectMapper.class);
        this.transferProcessingService = mock(TransferProcessingService.class);
        this.transferValidatedConsumer = new TransferValidatedConsumer(objectMapper, transferProcessingService);
    }

    @Test
    void consume_DeveProcessarTransferencia_QuandoEventoForAprovado() {
        LinkedHashMap<String, Object> payload = new LinkedHashMap<>();
        TransferValidatedEvent event = TransferValidatedEventStub.buildApprovedEvent();

        when(objectMapper.convertValue(payload, TransferValidatedEvent.class)).thenReturn(event);

        assertDoesNotThrow(() -> transferValidatedConsumer.consume(payload, 1, 10L));

        verify(objectMapper).convertValue(payload, TransferValidatedEvent.class);
        verify(transferProcessingService).process(event);
        verify(transferProcessingService, never()).reject(any());
    }

    @Test
    void consume_DeveRejeitarTransferencia_QuandoEventoNaoForAprovado() {
        LinkedHashMap<String, Object> payload = new LinkedHashMap<>();
        TransferValidatedEvent event = TransferValidatedEventStub.buildRejectedEvent();

        when(objectMapper.convertValue(payload, TransferValidatedEvent.class)).thenReturn(event);

        assertDoesNotThrow(() -> transferValidatedConsumer.consume(payload, 1, 10L));

        verify(objectMapper).convertValue(payload, TransferValidatedEvent.class);
        verify(transferProcessingService).reject(event);
        verify(transferProcessingService, never()).process(any());
    }

    @Test
    void consume_DeveExecutarSemLancarExcecao_QuandoOcorrerErroAoProcessarEvento() {
        LinkedHashMap<String, Object> payload = new LinkedHashMap<>();

        when(objectMapper.convertValue(payload, TransferValidatedEvent.class))
                .thenThrow(new RuntimeException("Erro ao converter payload"));

        assertDoesNotThrow(() -> transferValidatedConsumer.consume(payload, 1, 10L));

        verify(objectMapper).convertValue(payload, TransferValidatedEvent.class);
        verify(transferProcessingService, never()).process(any());
        verify(transferProcessingService, never()).reject(any());
    }
}