package com.bank_account.validator;

import com.bank_account.dto.response.ValidationResult;
import com.bank_account.entity.BankAccount;
import com.bank_account.event.TransferValidatedEvent;
import com.bank_account.stub.BankAccountStub;
import com.bank_account.stub.TransferValidatedEventStub;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToAccountActiveValidatorTest {

    private final ToAccountActiveValidator validator = new ToAccountActiveValidator();

    @Test
    void validate_DeveRetornarFailure_QuandoContaDeDestinoNaoEstiverAtiva() {
        TransferValidatedEvent event = TransferValidatedEventStub.buildApprovedEvent();
        BankAccount toAccount = BankAccountStub.buildEntity();
        toAccount.setStatus("INACTIVE");

        ValidationResult result = validator.validate(event, BankAccountStub.buildEntity(), toAccount);

        assertFalse(result.isValid());
        assertEquals("Conta de destino não está ativa", result.getErrorMessage());
    }

    @Test
    void validate_DeveRetornarSuccess_QuandoContaDeDestinoEstiverAtiva() {
        TransferValidatedEvent event = TransferValidatedEventStub.buildApprovedEvent();
        BankAccount toAccount = BankAccountStub.buildEntity();
        toAccount.setStatus("ACTIVE");

        ValidationResult result = validator.validate(event, BankAccountStub.buildEntity(), toAccount);

        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    void getOrder_DeveRetornar5() {
        assertEquals(5, validator.getOrder());
    }
}