package com.transfer.service;

import com.transfer.dto.request.CreatePixTransferRequest;
import com.transfer.dto.response.PixResponse;
import com.transfer.entity.PixTransfer;
import com.transfer.enums.TransferStatus;
import com.transfer.event.TransferInitiatedEvent;
import com.transfer.mapper.TransferMapper;
import com.transfer.producer.TransferEventProducer;
import com.transfer.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferMapper transferMapper;
    private final TransferRepository transferRepository;
    private final TransferEventProducer transferEventProducer;

    @Transactional
    public PixResponse createPixTransferRequest(CreatePixTransferRequest request) {
        log.info("💸 Criando transferência PIX");

        PixTransfer pixTransfer = transferMapper.toEntity(request);
        pixTransfer.setStatus(TransferStatus.PENDING);

        PixTransfer savedTransfer = transferRepository.save(pixTransfer);
        log.info("✅ Transferência PIX salva com ID: {}", savedTransfer.getId());

        TransferInitiatedEvent event = transferMapper.toEvent(savedTransfer);

        transferEventProducer.sendInitiatedTransfer(event);


        return transferMapper.toResponse(savedTransfer);
    }
}