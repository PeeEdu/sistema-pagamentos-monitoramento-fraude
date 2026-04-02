package com.bank_account.service;

import com.bank_account.dto.response.ValidationResult;
import com.bank_account.entity.BankAccount;
import com.bank_account.event.TransferCompletedEvent;
import com.bank_account.event.TransferValidatedEvent;
import com.bank_account.mapper.TransferEventMapper;
import com.bank_account.producer.TransferCompletedProducer;
import com.bank_account.repository.BankAccountRepository;
import com.bank_account.stub.BankAccountStub;
import com.bank_account.stub.TransferCompletedEventStub;
import com.bank_account.stub.TransferValidatedEventStub;
import com.bank_account.stub.ValidationResultStub;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class TransferProcessingServiceTest {

    private final BankAccountRepository bankAccountRepository;
    private final TransferEventMapper transferEventMapper;
    private final TransferCompletedProducer transferCompletedProducer;
    private final TransferValidationService validationService;
    private final TransferProcessingService transferProcessingService;

    TransferProcessingServiceTest() {
        this.bankAccountRepository = mock(BankAccountRepository.class);
        this.transferEventMapper = mock(TransferEventMapper.class);
        this.transferCompletedProducer = mock(TransferCompletedProducer.class);
        this.validationService = mock(TransferValidationService.class);

        this.transferProcessingService = new TransferProcessingService(
                bankAccountRepository,
                transferEventMapper,
                transferCompletedProducer,
                validationService
        );
    }

    @Test
    void process_DeveDebitarCreditarSalvarContasEEnviarEventoCompleted_QuandoValidacaoForValida() {
        TransferValidatedEvent event = TransferValidatedEventStub.buildApprovedEvent();
        TransferCompletedEvent completedEvent = TransferCompletedEventStub.buildEvent();

        BankAccount fromAccount = BankAccountStub.buildEntity();
        BankAccount toAccount = BankAccountStub.buildEntity();
        toAccount.setUserId("user-456");
        toAccount.setAccountNumber("654321");
        toAccount.setBalance(new BigDecimal("500.00"));

        BigDecimal fromInitialBalance = fromAccount.getBalance();
        BigDecimal toInitialBalance = toAccount.getBalance();

        when(bankAccountRepository.findByAccountNumber(event.getFromAccountNumber())).thenReturn(fromAccount);
        when(bankAccountRepository.findByPixKeyKey(event.getPixKey())).thenReturn(Optional.of(toAccount));
        when(validationService.validateAll(event, fromAccount, toAccount)).thenReturn(ValidationResultStub.buildSuccess());
        when(transferEventMapper.toCompletedEvent(event)).thenReturn(completedEvent);

        transferProcessingService.process(event);

        assertEquals(fromInitialBalance.subtract(event.getAmount()), fromAccount.getBalance());
        assertEquals(toInitialBalance.add(event.getAmount()), toAccount.getBalance());
        assertEquals("COMPLETED", completedEvent.getStatus());
        assertNull(completedEvent.getFailureReason());

        verify(bankAccountRepository).findByAccountNumber(event.getFromAccountNumber());
        verify(bankAccountRepository).findByPixKeyKey(event.getPixKey());
        verify(validationService).validateAll(event, fromAccount, toAccount);
        verify(bankAccountRepository).save(fromAccount);
        verify(bankAccountRepository).save(toAccount);
        verify(transferEventMapper).toCompletedEvent(event);
        verify(transferCompletedProducer).sendTransferCompleted(completedEvent);
    }

    @Test
    void process_DeveEnviarEventoFailedENaoSalvarContas_QuandoValidacaoForInvalida() {
        TransferValidatedEvent event = TransferValidatedEventStub.buildApprovedEvent();
        TransferCompletedEvent completedEvent = TransferCompletedEventStub.buildEvent();

        BankAccount fromAccount = BankAccountStub.buildEntity();
        BankAccount toAccount = BankAccountStub.buildEntity();

        when(bankAccountRepository.findByAccountNumber(event.getFromAccountNumber())).thenReturn(fromAccount);
        when(bankAccountRepository.findByPixKeyKey(event.getPixKey())).thenReturn(Optional.of(toAccount));
        when(validationService.validateAll(event, fromAccount, toAccount))
                .thenReturn(ValidationResultStub.buildFailure("Saldo insuficiente"));
        when(transferEventMapper.toCompletedEvent(event)).thenReturn(completedEvent);

        transferProcessingService.process(event);

        assertEquals("FAILED", completedEvent.getStatus());
        assertEquals("Saldo insuficiente", completedEvent.getFailureReason());

        verify(bankAccountRepository).findByAccountNumber(event.getFromAccountNumber());
        verify(bankAccountRepository).findByPixKeyKey(event.getPixKey());
        verify(validationService).validateAll(event, fromAccount, toAccount);
        verify(bankAccountRepository, never()).save(fromAccount);
        verify(bankAccountRepository, never()).save(toAccount);
        verify(transferEventMapper).toCompletedEvent(event);
        verify(transferCompletedProducer).sendTransferCompleted(completedEvent);
    }

    @Test
    void reject_DeveEnviarEventoRejectedComRejectionReason_QuandoTransferenciaForRejeitada() {
        TransferValidatedEvent event = TransferValidatedEventStub.buildRejectedEvent();
        TransferCompletedEvent completedEvent = TransferCompletedEventStub.buildEvent();

        when(transferEventMapper.toCompletedEvent(event)).thenReturn(completedEvent);

        transferProcessingService.reject(event);

        assertEquals("REJECTED", completedEvent.getStatus());
        assertEquals(event.getRejectionReason(), completedEvent.getFailureReason());

        verify(transferEventMapper).toCompletedEvent(event);
        verify(transferCompletedProducer).sendTransferCompleted(completedEvent);
    }
}