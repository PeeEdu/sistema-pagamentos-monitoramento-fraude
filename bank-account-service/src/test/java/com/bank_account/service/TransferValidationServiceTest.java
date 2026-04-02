package com.bank_account.service;

import com.bank_account.dto.response.ValidationResult;
import com.bank_account.entity.BankAccount;
import com.bank_account.event.TransferValidatedEvent;
import com.bank_account.stub.BankAccountStub;
import com.bank_account.stub.TransferValidatedEventStub;
import com.bank_account.validator.TransferValidator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferValidationServiceTest {

    @Test
    void validateAll_DeveRetornarSuccess_QuandoTodosValidatorsForemValidos() {
        TransferValidator validator1 = mock(TransferValidator.class);
        TransferValidator validator2 = mock(TransferValidator.class);

        TransferValidatedEvent event = TransferValidatedEventStub.buildApprovedEvent();
        BankAccount fromAccount = BankAccountStub.buildEntity();
        BankAccount toAccount = BankAccountStub.buildEntity();

        when(validator1.getOrder()).thenReturn(1);
        when(validator2.getOrder()).thenReturn(2);

        when(validator1.validate(event, fromAccount, toAccount)).thenReturn(ValidationResult.success());
        when(validator2.validate(event, fromAccount, toAccount)).thenReturn(ValidationResult.success());

        TransferValidationService service = new TransferValidationService(List.of(validator1, validator2));

        ValidationResult result = service.validateAll(event, fromAccount, toAccount);

        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());

        verify(validator1).validate(event, fromAccount, toAccount);
        verify(validator2).validate(event, fromAccount, toAccount);
    }

    @Test
    void validateAll_DeveRetornarPrimeiroErro_QuandoAlgumValidatorForInvalido() {
        TransferValidator validator1 = mock(TransferValidator.class);
        TransferValidator validator2 = mock(TransferValidator.class);

        TransferValidatedEvent event = TransferValidatedEventStub.buildApprovedEvent();
        BankAccount fromAccount = BankAccountStub.buildEntity();
        BankAccount toAccount = BankAccountStub.buildEntity();

        when(validator1.getOrder()).thenReturn(1);
        when(validator2.getOrder()).thenReturn(2);

        when(validator1.validate(event, fromAccount, toAccount))
                .thenReturn(ValidationResult.failure("Saldo insuficiente"));
        when(validator2.validate(event, fromAccount, toAccount))
                .thenReturn(ValidationResult.success());

        TransferValidationService service = new TransferValidationService(List.of(validator1, validator2));

        ValidationResult result = service.validateAll(event, fromAccount, toAccount);

        assertFalse(result.isValid());
        assertEquals("Saldo insuficiente", result.getErrorMessage());

        verify(validator1).validate(event, fromAccount, toAccount);
        verify(validator2, never()).validate(event, fromAccount, toAccount);
    }

    @Test
    void validateAll_DeveExecutarValidatorsNaOrdemCorreta_QuandoPossuiremOrdersDiferentes() {
        TransferValidator validator1 = mock(TransferValidator.class);
        TransferValidator validator2 = mock(TransferValidator.class);
        TransferValidator validator3 = mock(TransferValidator.class);

        TransferValidatedEvent event = TransferValidatedEventStub.buildApprovedEvent();
        BankAccount fromAccount = BankAccountStub.buildEntity();
        BankAccount toAccount = BankAccountStub.buildEntity();

        when(validator1.getOrder()).thenReturn(3);
        when(validator2.getOrder()).thenReturn(1);
        when(validator3.getOrder()).thenReturn(2);

        when(validator1.validate(event, fromAccount, toAccount)).thenReturn(ValidationResult.success());
        when(validator2.validate(event, fromAccount, toAccount)).thenReturn(ValidationResult.success());
        when(validator3.validate(event, fromAccount, toAccount)).thenReturn(ValidationResult.success());

        TransferValidationService service = new TransferValidationService(List.of(validator1, validator2, validator3));

        service.validateAll(event, fromAccount, toAccount);

        var inOrder = inOrder(validator2, validator3, validator1);
        inOrder.verify(validator2).validate(event, fromAccount, toAccount);
        inOrder.verify(validator3).validate(event, fromAccount, toAccount);
        inOrder.verify(validator1).validate(event, fromAccount, toAccount);
    }

    @Test
    void validateAll_DevePararNoPrimeiroErro_QuandoEncontrarValidacaoInvalida() {
        TransferValidator validator1 = mock(TransferValidator.class);
        TransferValidator validator2 = mock(TransferValidator.class);
        TransferValidator validator3 = mock(TransferValidator.class);

        TransferValidatedEvent event = TransferValidatedEventStub.buildApprovedEvent();
        BankAccount fromAccount = BankAccountStub.buildEntity();
        BankAccount toAccount = BankAccountStub.buildEntity();

        when(validator1.getOrder()).thenReturn(1);
        when(validator2.getOrder()).thenReturn(2);
        when(validator3.getOrder()).thenReturn(3);

        when(validator1.validate(event, fromAccount, toAccount)).thenReturn(ValidationResult.success());
        when(validator2.validate(event, fromAccount, toAccount))
                .thenReturn(ValidationResult.failure("Conta destino inválida"));
        when(validator3.validate(event, fromAccount, toAccount)).thenReturn(ValidationResult.success());

        TransferValidationService service = new TransferValidationService(List.of(validator1, validator2, validator3));

        ValidationResult result = service.validateAll(event, fromAccount, toAccount);

        assertFalse(result.isValid());
        assertEquals("Conta destino inválida", result.getErrorMessage());

        verify(validator1).validate(event, fromAccount, toAccount);
        verify(validator2).validate(event, fromAccount, toAccount);
        verify(validator3, never()).validate(event, fromAccount, toAccount);
    }
}