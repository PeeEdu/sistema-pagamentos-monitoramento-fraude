package com.fraud.mapper;

import com.fraud.entity.FraudEntity;
import com.fraud.enums.FraudStatus;
import com.fraud.enums.FraudType;
import com.fraud.event.TransferInitiatedEvent;
import com.fraud.event.TransferValidatedEvent;
import com.fraud.stub.TransferInitiatedEventStub;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FraudMapperTest {

    private final FraudMapper fraudMapper = Mappers.getMapper(FraudMapper.class);

    @Test
    void toValidatedEvent_DeveRetornarTransferValidatedEvent_QuandoTransferenciaForAprovada() {
        TransferInitiatedEvent event = TransferInitiatedEventStub.buildEvent();
        List<FraudType> fraudTypes = List.of();

        LocalDateTime before = LocalDateTime.now();
        TransferValidatedEvent response = fraudMapper.toValidatedEvent(event, true, 10.0, fraudTypes);
        LocalDateTime after = LocalDateTime.now();

        assertNotNull(response);
        assertEquals(event.getTransferId(), response.getTransferId());
        assertEquals(event.getFromAccountNumber(), response.getFromAccountNumber());
        assertEquals(event.getPixKey(), response.getPixKey());
        assertEquals(event.getAmount(), response.getAmount());
        assertEquals(event.getDescription(), response.getDescription());
        assertEquals(event.getInitiatedBy(), response.getInitiatedBy());
        assertTrue(response.isApproved());
        assertEquals(10.0, response.getRiskScore());
        assertEquals(fraudTypes, response.getFraudTypes());
        assertNull(response.getRejectionReason());
        assertNotNull(response.getValidatedAt());
        assertFalse(response.getValidatedAt().isBefore(before));
        assertFalse(response.getValidatedAt().isAfter(after));
    }

    @Test
    void toValidatedEvent_DeveRetornarTransferValidatedEventComRejectionReason_QuandoTransferenciaNaoForAprovada() {
        TransferInitiatedEvent event = TransferInitiatedEventStub.buildEvent();
        List<FraudType> fraudTypes = List.of(FraudType.HIGH_VALUE);

        TransferValidatedEvent response = fraudMapper.toValidatedEvent(event, false, 95.0, fraudTypes);

        assertNotNull(response);
        assertEquals(event.getTransferId(), response.getTransferId());
        assertEquals(event.getFromAccountNumber(), response.getFromAccountNumber());
        assertEquals(event.getPixKey(), response.getPixKey());
        assertEquals(event.getAmount(), response.getAmount());
        assertEquals(event.getDescription(), response.getDescription());
        assertEquals(event.getInitiatedBy(), response.getInitiatedBy());
        assertFalse(response.isApproved());
        assertEquals(95.0, response.getRiskScore());
        assertEquals(fraudTypes, response.getFraudTypes());
        assertEquals("Fraude detectada: " + fraudTypes, response.getRejectionReason());
        assertNotNull(response.getValidatedAt());
    }

    @Test
    void toFraudEntity_DeveRetornarFraudEntity_QuandoReceberEventoERisco() {
        TransferInitiatedEvent event = TransferInitiatedEventStub.buildEvent();
        List<FraudType> fraudTypes = List.of(FraudType.HIGH_VALUE);

        LocalDateTime before = LocalDateTime.now();
        FraudEntity response = fraudMapper.toFraudEntity(event, 85.5, fraudTypes);
        LocalDateTime after = LocalDateTime.now();

        assertNotNull(response);
        assertNull(response.getId());
        assertEquals(event.getTransferId(), response.getTransactionId());
        assertEquals(event.getAmount(), response.getAmount());
        assertEquals(fraudTypes, response.getFraudTypes());
        assertEquals(FraudStatus.DETECTED, response.getStatus());
        assertNull(response.getRiskLevel());
        assertEquals(85.5, response.getRiskScore());
        assertNotNull(response.getDetectedAt());
        assertFalse(response.getDetectedAt().isBefore(before));
        assertFalse(response.getDetectedAt().isAfter(after));
        assertNull(response.getUpdatedAt());
        assertEquals("RULE_BASED", response.getDetectionMethod());
        assertNull(response.getTriggeredRules());
        assertNull(response.getMetadata());
        assertNull(response.getIpAddress());
        assertNull(response.getDeviceId());
        assertNull(response.getLocation());
        assertNull(response.getReviewedBy());
        assertNull(response.getReviewedAt());
        assertNull(response.getObservation());
    }

    @Test
    void buildRejectionReason_DeveRetornarNulo_QuandoTransferenciaForAprovada() {
        String response = fraudMapper.buildRejectionReason(true, List.of(FraudType.HIGH_VALUE));

        assertNull(response);
    }

    @Test
    void buildRejectionReason_DeveRetornarMensagem_QuandoTransferenciaNaoForAprovada() {
        List<FraudType> fraudTypes = List.of(FraudType.HIGH_VALUE, FraudType.HIGH_FREQUENCY);

        String response = fraudMapper.buildRejectionReason(false, fraudTypes);

        assertEquals("Fraude detectada: " + fraudTypes, response);
    }
}