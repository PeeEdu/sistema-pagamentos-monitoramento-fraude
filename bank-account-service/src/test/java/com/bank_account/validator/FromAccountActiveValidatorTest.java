package com.bank_account.validator;

import com.bank_account.dto.response.ValidationResult;
import com.bank_account.entity.BankAccount;
import com.bank_account.event.TransferValidatedEvent;
import com.bank_account.stub.BankAccountStub;
import com.bank_account.stub.TransferValidatedEventStub;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FromAccountActiveValidatorTest {

    private final FromAccountActiveValidator validator = new FromAccountActiveValidator();

    @Test
    void validate_DeveRetornarFailure_QuandoContaDeOrigemNaoEstiverAtiva() {
        TransferValidatedEvent event = TransferValidatedEventStub.buildApprovedEvent();
        BankAccount fromAccount = BankAccountStub.buildEntity();
        fromAccount.setStatus("INACTIVE");

        ValidationResult result = validator.validate(event, fromAccount, BankAccountStub.buildEntity());

        assertFalse(result.isValid());
        assertEquals("Conta de origem não está ativa", result.getErrorMessage());
    }

    @Test
    void validate_DeveRetornarSuccess_QuandoContaDeOrigemEstiverAtiva() {
        TransferValidatedEvent event = TransferValidatedEventStub.buildApprovedEvent();
        BankAccount fromAccount = BankAccountStub.buildEntity();
        fromAccount.setStatus("ACTIVE");

        ValidationResult result = validator.validate(event, fromAccount, BankAccountStub.buildEntity());

        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    void getOrder_DeveRetornar4() {
        assertEquals(4, validator.getOrder());
    }
}