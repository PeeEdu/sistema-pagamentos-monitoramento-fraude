package com.bank_account.consumer;

import com.bank_account.event.UserCreatedEvent;
import com.bank_account.service.BankAccountService;
import com.bank_account.stub.UserCreatedEventStub;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CreateBankAccountConsumerTest {

    private final BankAccountService bankAccountService;
    private final ObjectMapper objectMapper;
    private final CreateBankAccountConsumer createBankAccountConsumer;

    CreateBankAccountConsumerTest() {
        this.bankAccountService = mock(BankAccountService.class);
        this.objectMapper = mock(ObjectMapper.class);
        this.createBankAccountConsumer = new CreateBankAccountConsumer(bankAccountService, objectMapper);
    }

    @Test
    void consumeUserCreatedEvent_DeveCriarConta_QuandoEventoForValido() {
        LinkedHashMap<String, Object> payload = new LinkedHashMap<>();
        UserCreatedEvent event = UserCreatedEventStub.buildEvent();

        when(objectMapper.convertValue(payload, UserCreatedEvent.class)).thenReturn(event);

        assertDoesNotThrow(() ->
                createBankAccountConsumer.consumeUserCreatedEvent(payload, 1, 10L)
        );

        verify(objectMapper).convertValue(payload, UserCreatedEvent.class);
        verify(bankAccountService).createAccountForUser(event);
    }

    @Test
    void consumeUserCreatedEvent_DeveLancarRuntimeException_QuandoOcorrerErroAoProcessarEvento() {
        LinkedHashMap<String, Object> payload = new LinkedHashMap<>();

        when(objectMapper.convertValue(payload, UserCreatedEvent.class))
                .thenThrow(new RuntimeException("Erro ao converter payload"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> createBankAccountConsumer.consumeUserCreatedEvent(payload, 1, 10L)
        );

        assertEquals("Erro ao processar evento de criação de usuário", exception.getMessage());
        assertEquals("Erro ao converter payload", exception.getCause().getMessage());

        verify(objectMapper).convertValue(payload, UserCreatedEvent.class);
        verify(bankAccountService, never()).createAccountForUser(any());
    }
}