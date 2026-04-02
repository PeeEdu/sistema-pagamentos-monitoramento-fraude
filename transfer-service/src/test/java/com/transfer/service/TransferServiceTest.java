package com.transfer.service;

import com.transfer.dto.request.CreatePixTransferRequest;
import com.transfer.dto.response.PixResponse;
import com.transfer.entity.PixTransfer;
import com.transfer.entity.Transfer;
import com.transfer.enums.TransferStatus;
import com.transfer.event.TransferCompletedEvent;
import com.transfer.event.TransferInitiatedEvent;
import com.transfer.mapper.TransferMapper;
import com.transfer.producer.TransferEventProducer;
import com.transfer.repository.TransferRepository;
import com.transfer.stub.CreatePixTransferRequestStub;
import com.transfer.stub.PixResponseStub;
import com.transfer.stub.PixTransferStub;
import com.transfer.stub.TransferCompletedEventStub;
import com.transfer.stub.TransferInitiatedEventStub;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class TransferServiceTest {

    private final TransferMapper transferMapper;
    private final TransferRepository transferRepository;
    private final TransferEventProducer transferEventProducer;
    private final TransferService transferService;

    TransferServiceTest() {
        this.transferMapper = mock(TransferMapper.class);
        this.transferRepository = mock(TransferRepository.class);
        this.transferEventProducer = mock(TransferEventProducer.class);
        this.transferService = new TransferService(
                transferMapper,
                transferRepository,
                transferEventProducer
        );
    }

    @Test
    void createPixTransferRequest_DeveCriarSalvarEnviarEventoERetornarResponse_QuandoRequestForValido() {
        CreatePixTransferRequest request = CreatePixTransferRequestStub.buildRequest();
        PixTransfer pixTransfer = PixTransferStub.buildEntity();
        PixTransfer savedTransfer = PixTransferStub.buildEntity();
        TransferInitiatedEvent event = TransferInitiatedEventStub.buildEvent();
        PixResponse response = PixResponseStub.buildResponse();

        when(transferMapper.toEntity(request)).thenReturn(pixTransfer);
        when(transferRepository.save(pixTransfer)).thenReturn(savedTransfer);
        when(transferMapper.toEvent(savedTransfer)).thenReturn(event);
        when(transferMapper.toResponse(savedTransfer)).thenReturn(response);

        PixResponse result = transferService.createPixTransferRequest(request);

        assertNotNull(result);
        assertEquals(response, result);

        verify(transferMapper).toEntity(request);
        verify(transferRepository).save(pixTransfer);
        verify(transferMapper).toEvent(savedTransfer);
        verify(transferEventProducer).sendInitiatedTransfer(event);
        verify(transferMapper).toResponse(savedTransfer);
    }

    @Test
    void createPixTransferRequest_DeveDefinirStatusPending_QuandoCriarTransferencia() {
        CreatePixTransferRequest request = CreatePixTransferRequestStub.buildRequest();
        PixTransfer pixTransfer = PixTransferStub.buildEntity();
        PixTransfer savedTransfer = PixTransferStub.buildEntity();
        TransferInitiatedEvent event = TransferInitiatedEventStub.buildEvent();
        PixResponse response = PixResponseStub.buildResponse();

        pixTransfer.setStatus(TransferStatus.COMPLETED);

        when(transferMapper.toEntity(request)).thenReturn(pixTransfer);
        when(transferRepository.save(any(PixTransfer.class))).thenReturn(savedTransfer);
        when(transferMapper.toEvent(savedTransfer)).thenReturn(event);
        when(transferMapper.toResponse(savedTransfer)).thenReturn(response);

        transferService.createPixTransferRequest(request);

        verify(transferRepository).save(argThat(transfer ->
                transfer.getStatus() == TransferStatus.PENDING
        ));
    }

    @Test
    void updateStatus_DeveAtualizarStatus_QuandoTransferenciaExistir() throws Exception {
        TransferCompletedEvent event = TransferCompletedEventStub.buildEvent();
        Transfer transfer = PixTransferStub.buildEntity();

        when(transferRepository.findById(event.getTransferId())).thenReturn(Optional.of(transfer));

        transferService.updateStatus(event);

        assertEquals(TransferStatus.COMPLETED, transfer.getStatus());
        verify(transferRepository).findById(event.getTransferId());
        verify(transferRepository).save(transfer);
    }

    @Test
    void updateStatus_DeveLancarException_QuandoTransferenciaNaoExistir() {
        TransferCompletedEvent event = TransferCompletedEventStub.buildEvent();

        when(transferRepository.findById(event.getTransferId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                Exception.class,
                () -> transferService.updateStatus(event)
        );

        assertEquals("Pix não encontrado", exception.getMessage());

        verify(transferRepository).findById(event.getTransferId());
        verify(transferRepository, never()).save(any());
    }
}