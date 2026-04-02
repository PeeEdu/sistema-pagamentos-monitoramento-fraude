package com.transfer.mapper;

import com.transfer.dto.request.CreatePixTransferRequest;
import com.transfer.dto.response.PixResponse;
import com.transfer.entity.PixTransfer;
import com.transfer.event.TransferInitiatedEvent;
import com.transfer.stub.CreatePixTransferRequestStub;
import com.transfer.stub.PixTransferStub;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransferMapperTest {

    private final TransferMapper transferMapper = Mappers.getMapper(TransferMapper.class);

    @Test
    void toEntity_DeveRetornarPixTransfer_QuandoReceberCreatePixTransferRequest() {
        CreatePixTransferRequest request = CreatePixTransferRequestStub.buildRequest();

        PixTransfer response = transferMapper.toEntity(request);

        assertNotNull(response);
        assertNull(response.getId());
        assertEquals(request.fromAccountNumber(), response.getFromAccountNumber());
        assertEquals(request.pix().key(), response.getPixKey());
        assertEquals(request.pix().type(), response.getPixKeyType());
        assertEquals(request.amount(), response.getAmount());
        assertEquals(request.description(), response.getDescription());
        assertEquals(request.initiatedBy(), response.getInitiatedBy());
        assertEquals(request.geoLocalization(), response.getGeoLocalization());
        assertEquals("PENDING", response.getStatus().name());
        assertNull(response.getFailureReason());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());
    }

    @Test
    void toResponse_DeveRetornarPixResponse_QuandoReceberPixTransfer() {
        PixTransfer transfer = PixTransferStub.buildEntity();

        PixResponse response = transferMapper.toResponse(transfer);

        assertNotNull(response);
        assertEquals(transfer.getId(), response.id());
        assertEquals(transfer.getFromAccountNumber(), response.fromAccountNumber());
        assertEquals(transfer.getPixKey(), response.pixKey());
        assertEquals(transfer.getPixKeyType(), response.pixKeyType());
        assertEquals(transfer.getAmount(), response.amount());
        assertEquals(transfer.getDescription(), response.description());
        assertEquals(transfer.getStatus(), response.status());
        assertEquals(transfer.getInitiatedBy(), response.initiatedBy());
        assertEquals(transfer.getGeoLocalization(), response.geoLocalization());
        assertEquals(transfer.getCreatedAt(), response.createdAt());
        assertEquals(transfer.getUpdatedAt(), response.updatedAt());
    }

    @Test
    void toEvent_DeveRetornarTransferInitiatedEvent_QuandoReceberPixTransfer() {
        PixTransfer transfer = PixTransferStub.buildEntity();

        LocalDateTime before = LocalDateTime.now();
        TransferInitiatedEvent response = transferMapper.toEvent(transfer);
        LocalDateTime after = LocalDateTime.now();

        assertNotNull(response);
        assertEquals(transfer.getId(), response.getTransferId());
        assertEquals(transfer.getFromAccountNumber(), response.getFromAccountNumber());
        assertEquals(transfer.getPixKey(), response.getPixKey());
        assertEquals(transfer.getPixKeyType(), response.getPixKeyType());
        assertEquals(transfer.getAmount(), response.getAmount());
        assertEquals(transfer.getDescription(), response.getDescription());
        assertEquals(transfer.getInitiatedBy(), response.getInitiatedBy());
        assertEquals(transfer.getGeoLocalization(), response.getGeoLocalization());
        assertNotNull(response.getInitiatedAt());
        assertFalse(response.getInitiatedAt().isBefore(before));
        assertFalse(response.getInitiatedAt().isAfter(after));
    }

    @Test
    void toEntity_DeveRetornarNulo_QuandoRequestForNulo() {
        PixTransfer response = transferMapper.toEntity(null);

        assertNull(response);
    }

    @Test
    void toResponse_DeveRetornarNulo_QuandoTransferForNulo() {
        PixResponse response = transferMapper.toResponse(null);

        assertNull(response);
    }

    @Test
    void toEvent_DeveRetornarNulo_QuandoTransferForNulo() {
        TransferInitiatedEvent response = transferMapper.toEvent(null);

        assertNull(response);
    }
}