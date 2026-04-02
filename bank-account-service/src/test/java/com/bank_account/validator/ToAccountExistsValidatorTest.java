package com.bank_account.validator;

import com.bank_account.dto.response.ValidationResult;
import com.bank_account.entity.BankAccount;
import com.bank_account.event.TransferValidatedEvent;
import com.bank_account.stub.BankAccountStub;
import com.bank_account.stub.TransferValidatedEventStub;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToAccountExistsValidatorTest {

    private final ToAccountExistsValidator validator = new ToAccountExistsValidator();

    @Test
    void validate_DeveRetornarFailure_QuandoContaDeDestinoNaoExistir() {
        TransferValidatedEvent event = TransferValidatedEventStub.buildApprovedEvent();

        ValidationResult result = validator.validate(event, BankAccountStub.buildEntity(), null);

        assertFalse(result.isValid());
        assertEquals("Chave PIX não encontrada", result.getErrorMessage());
    }

    @Test
    void validate_DeveRetornarSuccess_QuandoContaDeDestinoExistir() {
        TransferValidatedEvent event = TransferValidatedEventStub.buildApprovedEvent();
        BankAccount toAccount = BankAccountStub.buildEntity();

        ValidationResult result = validator.validate(event, BankAccountStub.buildEntity(), toAccount);

        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    void getOrder_DeveRetornar2() {
        assertEquals(2, validator.getOrder());
    }
}