package com.bank_account.producer;

import com.bank_account.entity.BankAccount;
import com.bank_account.entity.PixKey;
import com.bank_account.event.PixKeyCreatedEvent;
import com.bank_account.event.PixKeyDeletedEvent;
import com.bank_account.mapper.BankAccountMapper;
import com.bank_account.stub.BankAccountStub;
import com.bank_account.stub.PixKeyCreatedEventStub;
import com.bank_account.stub.PixKeyDeletedEventStub;
import com.bank_account.stub.PixKeyStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PixKeyEventProducerTest {

    private KafkaTemplate<String, Object> kafkaTemplate;
    private BankAccountMapper bankAccountMapper;
    private PixKeyEventProducer pixKeyEventProducer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        bankAccountMapper = mock(BankAccountMapper.class);
        pixKeyEventProducer = new PixKeyEventProducer(kafkaTemplate, bankAccountMapper);

        ReflectionTestUtils.setField(pixKeyEventProducer, "pixKeyCreatedTopic", "pix-key-created-topic");
        ReflectionTestUtils.setField(pixKeyEventProducer, "pixKeyDeletedTopic", "pix-key-deleted-topic");
    }

    @Test
    void sendPixKeyCreatedEvent_DeveMapearEEnviarEvento_QuandoDadosForemValidos() {
        BankAccount bankAccount = BankAccountStub.buildEntity();
        PixKey pixKey = PixKeyStub.buildEntity();
        PixKeyCreatedEvent event = PixKeyCreatedEventStub.buildEvent();

        when(bankAccountMapper.toPixKeyCreatedEvent(bankAccount, pixKey)).thenReturn(event);

        assertDoesNotThrow(() -> pixKeyEventProducer.sendPixKeyCreatedEvent(bankAccount, pixKey));

        verify(bankAccountMapper).toPixKeyCreatedEvent(bankAccount, pixKey);
        verify(kafkaTemplate).send("pix-key-created-topic", event);
    }

    @Test
    void sendPixKeyCreatedEvent_DeveRelancarExcecao_QuandoOcorrerErroAoEnviarEvento() {
        BankAccount bankAccount = BankAccountStub.buildEntity();
        PixKey pixKey = PixKeyStub.buildEntity();

        when(bankAccountMapper.toPixKeyCreatedEvent(bankAccount, pixKey))
                .thenThrow(new RuntimeException("Erro ao mapear evento"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> pixKeyEventProducer.sendPixKeyCreatedEvent(bankAccount, pixKey)
        );

        assertEquals("Erro ao mapear evento", exception.getMessage());

        verify(bankAccountMapper).toPixKeyCreatedEvent(bankAccount, pixKey);
        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    void sendPixKeyDeletedEvent_DeveMapearEEnviarEvento_QuandoDadosForemValidos() {
        BankAccount bankAccount = BankAccountStub.buildEntity();
        String pixKey = "joao.silva@email.com";
        PixKeyDeletedEvent event = PixKeyDeletedEventStub.buildEvent();

        when(bankAccountMapper.toPixKeyDeletedEvent(bankAccount, pixKey)).thenReturn(event);

        assertDoesNotThrow(() -> pixKeyEventProducer.sendPixKeyDeletedEvent(bankAccount, pixKey));

        verify(bankAccountMapper).toPixKeyDeletedEvent(bankAccount, pixKey);
        verify(kafkaTemplate).send("pix-key-deleted-topic", event);
    }

    @Test
    void sendPixKeyDeletedEvent_DeveRelancarExcecao_QuandoOcorrerErroAoEnviarEvento() {
        BankAccount bankAccount = BankAccountStub.buildEntity();
        String pixKey = "joao.silva@email.com";

        when(bankAccountMapper.toPixKeyDeletedEvent(bankAccount, pixKey))
                .thenThrow(new RuntimeException("Erro ao mapear evento"));

        assertThrows(
                RuntimeException.class,
                () -> pixKeyEventProducer.sendPixKeyDeletedEvent(bankAccount, pixKey)
        );

        verify(bankAccountMapper).toPixKeyDeletedEvent(bankAccount, pixKey);
        verify(kafkaTemplate, never()).send(anyString(), any());
    }
}