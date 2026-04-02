package com.bank_account.validator;

import com.bank_account.dto.response.ValidationResult;
import com.bank_account.entity.BankAccount;
import com.bank_account.event.TransferValidatedEvent;
import com.bank_account.stub.BankAccountStub;
import com.bank_account.stub.TransferValidatedEventStub;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FromAccountExistsValidatorTest {

    private final FromAccountExistsValidator validator = new FromAccountExistsValidator();

    @Test
    void validate_DeveRetornarFailure_QuandoContaDeOrigemNaoExistir() {
        TransferValidatedEvent event = TransferValidatedEventStub.buildApprovedEvent();

        ValidationResult result = validator.validate(event, null, BankAccountStub.buildEntity());

        assertFalse(result.isValid());
        assertEquals("Conta de origem não encontrada", result.getErrorMessage());
    }

    @Test
    void validate_DeveRetornarSuccess_QuandoContaDeOrigemExistir() {
        TransferValidatedEvent event = TransferValidatedEventStub.buildApprovedEvent();
        BankAccount fromAccount = BankAccountStub.buildEntity();

        ValidationResult result = validator.validate(event, fromAccount, BankAccountStub.buildEntity());

        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    void getOrder_DeveRetornar1() {
        assertEquals(1, validator.getOrder());
    }
}