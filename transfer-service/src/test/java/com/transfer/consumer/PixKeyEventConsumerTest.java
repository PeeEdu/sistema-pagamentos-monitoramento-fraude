package com.transfer.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.event.PixKeyCreatedEvent;
import com.transfer.event.PixKeyDeletedEvent;
import com.transfer.repository.AccountPixKeyRepository;
import com.transfer.stub.PixKeyCreatedEventStub;
import com.transfer.stub.PixKeyDeletedEventStub;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class PixKeyEventConsumerTest {

    private final AccountPixKeyRepository accountPixKeyRepository;
    private final ObjectMapper objectMapper;
    private final PixKeyEventConsumer pixKeyEventConsumer;

    PixKeyEventConsumerTest() {
        this.accountPixKeyRepository = mock(AccountPixKeyRepository.class);
        this.objectMapper = mock(ObjectMapper.class);
        this.pixKeyEventConsumer = new PixKeyEventConsumer(accountPixKeyRepository, objectMapper);
    }

    @Test
    void handlePixKeyCreated_DeveSalvarChavePix_QuandoChaveNaoExistir() {
        LinkedHashMap<String, Object> eventData = new LinkedHashMap<>();
        PixKeyCreatedEvent event = PixKeyCreatedEventStub.buildEvent();

        when(objectMapper.convertValue(eventData, PixKeyCreatedEvent.class)).thenReturn(event);
        when(accountPixKeyRepository.existsByPixKey(event.getPixKey())).thenReturn(false);

        assertDoesNotThrow(() -> pixKeyEventConsumer.handlePixKeyCreated(eventData));

        verify(objectMapper).convertValue(eventData, PixKeyCreatedEvent.class);
        verify(accountPixKeyRepository).existsByPixKey(event.getPixKey());
        verify(accountPixKeyRepository).save(argThat(accountPixKey ->
                accountPixKey.getUserId().equals(event.getUserId()) &&
                        accountPixKey.getAccountNumber().equals(event.getAccountNumber()) &&
                        accountPixKey.getPixKey().equals(event.getPixKey()) &&
                        accountPixKey.getPixKeyType().equals(event.getPixKeyType())
        ));
    }

    @Test
    void handlePixKeyCreated_DeveNaoSalvarChavePix_QuandoChaveJaExistir() {
        LinkedHashMap<String, Object> eventData = new LinkedHashMap<>();
        PixKeyCreatedEvent event = PixKeyCreatedEventStub.buildEvent();

        when(objectMapper.convertValue(eventData, PixKeyCreatedEvent.class)).thenReturn(event);
        when(accountPixKeyRepository.existsByPixKey(event.getPixKey())).thenReturn(true);

        assertDoesNotThrow(() -> pixKeyEventConsumer.handlePixKeyCreated(eventData));

        verify(objectMapper).convertValue(eventData, PixKeyCreatedEvent.class);
        verify(accountPixKeyRepository).existsByPixKey(event.getPixKey());
        verify(accountPixKeyRepository, never()).save(any());
    }

    @Test
    void handlePixKeyCreated_DeveExecutarSemLancarExcecao_QuandoOcorrerErroAoProcessarEvento() {
        LinkedHashMap<String, Object> eventData = new LinkedHashMap<>();

        when(objectMapper.convertValue(eventData, PixKeyCreatedEvent.class))
                .thenThrow(new RuntimeException("Erro ao converter evento"));

        assertDoesNotThrow(() -> pixKeyEventConsumer.handlePixKeyCreated(eventData));

        verify(objectMapper).convertValue(eventData, PixKeyCreatedEvent.class);
        verify(accountPixKeyRepository, never()).save(any());
    }

    @Test
    void handlePixKeyDeleted_DeveRemoverChavePix_QuandoEventoForValido() {
        LinkedHashMap<String, Object> eventData = new LinkedHashMap<>();
        PixKeyDeletedEvent event = PixKeyDeletedEventStub.buildEvent();

        when(objectMapper.convertValue(eventData, PixKeyDeletedEvent.class)).thenReturn(event);

        assertDoesNotThrow(() -> pixKeyEventConsumer.handlePixKeyDeleted(eventData));

        verify(objectMapper).convertValue(eventData, PixKeyDeletedEvent.class);
        verify(accountPixKeyRepository).deleteByPixKey(event.getPixKey());
    }

    @Test
    void handlePixKeyDeleted_DeveExecutarSemLancarExcecao_QuandoOcorrerErroAoProcessarEvento() {
        LinkedHashMap<String, Object> eventData = new LinkedHashMap<>();

        when(objectMapper.convertValue(eventData, PixKeyDeletedEvent.class))
                .thenThrow(new RuntimeException("Erro ao converter evento"));

        assertDoesNotThrow(() -> pixKeyEventConsumer.handlePixKeyDeleted(eventData));

        verify(objectMapper).convertValue(eventData, PixKeyDeletedEvent.class);
        verify(accountPixKeyRepository, never()).deleteByPixKey(anyString());
    }
}