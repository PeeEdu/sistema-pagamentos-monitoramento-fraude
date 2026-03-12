package com.bank_account.service;

import com.bank_account.dto.response.ValidationResult;
import com.bank_account.entity.BankAccount;
import com.bank_account.event.TransferCompletedEvent;
import com.bank_account.event.TransferValidatedEvent;
import com.bank_account.mapper.TransferEventMapper;
import com.bank_account.producer.TransferCompletedProducer;
import com.bank_account.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferProcessingService {

    private final BankAccountRepository bankAccountRepository;
    private final TransferEventMapper transferEventMapper;
    private final TransferCompletedProducer transferCompletedProducer;
    private final TransferValidationService validationService;

    @Transactional
    public void process(TransferValidatedEvent event) {
        log.info("🔍 Processando transferência: {}", event.getTransferId());

        BankAccount fromAccount = bankAccountRepository.findByAccountNumber(event.getFromAccountNumber());
        BankAccount toAccount = bankAccountRepository.findByPixKeyKey(event.getPixKey()).orElse(null);

        ValidationResult validation = validationService.validateAll(event, fromAccount, toAccount);

        if (!validation.isValid()) {
            sendCompletion(event, "FAILED", validation.getErrorMessage());
            return;
        }

        fromAccount.withdraw(event.getAmount());
        Objects.requireNonNull(toAccount).deposit(event.getAmount());

        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);

        log.info("✅ Transferência realizada com sucesso");
        log.info("   Conta origem - Novo saldo: {}", fromAccount.getBalance());
        log.info("   Conta destino - Novo saldo: {}", toAccount.getBalance());

        sendCompletion(event, "COMPLETED", null);
    }

    public void reject(TransferValidatedEvent event) {
        log.warn("Rejeitando transferência: {}", event.getTransferId());
        sendCompletion(event, "REJECTED", event.getRejectionReason());
    }

    private void sendCompletion(TransferValidatedEvent event, String status, String failureReason) {
        TransferCompletedEvent completedEvent = transferEventMapper.toCompletedEvent(event);
        completedEvent.setStatus(status);
        completedEvent.setFailureReason(failureReason);

        transferCompletedProducer.sendTransferCompleted(completedEvent);

        log.info("Evento enviado - Status: {}, Reason: {}", status, failureReason);
    }
}