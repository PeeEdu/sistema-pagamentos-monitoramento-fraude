package com.transfer.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.event.TransferCompletedEvent;
import com.transfer.service.TransferService;
import com.transfer.stub.TransferCompletedEventStub;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TransferCompletedConsumerTest {

    private final TransferService transferService;
    private final ObjectMapper objectMapper;
    private final TransferCompletedConsumer transferCompletedConsumer;

    TransferCompletedConsumerTest() {
        this.transferService = mock(TransferService.class);
        this.objectMapper = mock(ObjectMapper.class);
        this.transferCompletedConsumer = new TransferCompletedConsumer(transferService, objectMapper);
    }

    @Test
    void consumeUserCreatedEvent_DeveAtualizarStatus_QuandoEventoForValido() throws Exception {
        LinkedHashMap<String, Object> payload = new LinkedHashMap<>();
        TransferCompletedEvent event = TransferCompletedEventStub.buildEvent();

        when(objectMapper.convertValue(payload, TransferCompletedEvent.class)).thenReturn(event);

        assertDoesNotThrow(() ->
                transferCompletedConsumer.consumeUserCreatedEvent(payload, 1, 10L)
        );

        verify(objectMapper).convertValue(payload, TransferCompletedEvent.class);
        verify(transferService).updateStatus(event);
    }

    @Test
    void consumeUserCreatedEvent_DeveLancarRuntimeException_QuandoOcorrerErroAoProcessarEvento() throws Exception {
        LinkedHashMap<String, Object> payload = new LinkedHashMap<>();

        when(objectMapper.convertValue(payload, TransferCompletedEvent.class))
                .thenThrow(new RuntimeException("Erro ao converter payload"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> transferCompletedConsumer.consumeUserCreatedEvent(payload, 1, 10L)
        );

        assertEquals("Erro ao processar evento de transferência concluída", exception.getMessage());
        assertEquals("Erro ao converter payload", exception.getCause().getMessage());

        verify(objectMapper).convertValue(payload, TransferCompletedEvent.class);
        verify(transferService, never()).updateStatus(any());
    }
}