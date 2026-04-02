package com.bank_account.mapper;

import com.bank_account.event.TransferCompletedEvent;
import com.bank_account.event.TransferValidatedEvent;
import com.bank_account.stub.TransferValidatedEventStub;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransferEventMapperTest {

    private final TransferEventMapper transferEventMapper =
            Mappers.getMapper(TransferEventMapper.class);

    @Test
    void toCompletedEvent_DeveRetornarTransferCompletedEvent_QuandoReceberTransferValidatedEvent() {
        TransferValidatedEvent validatedEvent = TransferValidatedEventStub.buildApprovedEvent();

        LocalDateTime before = LocalDateTime.now();
        TransferCompletedEvent response = transferEventMapper.toCompletedEvent(validatedEvent);
        LocalDateTime after = LocalDateTime.now();

        assertNotNull(response);
        assertEquals(validatedEvent.getTransferId(), response.getTransferId());
        assertEquals(validatedEvent.getFromAccountNumber(), response.getFromAccountNumber());
        assertEquals(validatedEvent.getPixKey(), response.getToAccountId());
        assertEquals(validatedEvent.getAmount(), response.getAmount());
        assertEquals(validatedEvent.getDescription(), response.getDescription());
        assertEquals(validatedEvent.getInitiatedBy(), response.getInitiatedBy());
        assertEquals("N/A", response.getGeoLocalization());
        assertNull(response.getStatus());
        assertNull(response.getFailureReason());
        assertNotNull(response.getCompletedAt());
        assertFalse(response.getCompletedAt().isBefore(before));
        assertFalse(response.getCompletedAt().isAfter(after));
    }

    @Test
    void toCompletedEvent_DeveRetornarNulo_QuandoEventoForNulo() {
        TransferCompletedEvent response = transferEventMapper.toCompletedEvent(null);

        assertNull(response);
    }
}