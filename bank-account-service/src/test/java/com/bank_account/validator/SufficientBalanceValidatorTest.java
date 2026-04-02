package com.bank_account.validator;

import com.bank_account.dto.response.ValidationResult;
import com.bank_account.entity.BankAccount;
import com.bank_account.event.TransferValidatedEvent;
import com.bank_account.stub.BankAccountStub;
import com.bank_account.stub.TransferValidatedEventStub;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SufficientBalanceValidatorTest {

    private final SufficientBalanceValidator validator = new SufficientBalanceValidator();

    @Test
    void validate_DeveRetornarFailure_QuandoSaldoForInsuficiente() {
        TransferValidatedEvent event = TransferValidatedEventStub.buildApprovedEvent();
        BankAccount fromAccount = BankAccountStub.buildEntity();
        fromAccount.setBalance(new BigDecimal("50.00"));

        ValidationResult result = validator.validate(event, fromAccount, BankAccountStub.buildEntity());

        assertFalse(result.isValid());
        assertEquals("Saldo insuficiente", result.getErrorMessage());
    }

    @Test
    void validate_DeveRetornarSuccess_QuandoSaldoForSuficiente() {
        TransferValidatedEvent event = TransferValidatedEventStub.buildApprovedEvent();
        BankAccount fromAccount = BankAccountStub.buildEntity();
        fromAccount.setBalance(new BigDecimal("1000.00"));

        ValidationResult result = validator.validate(event, fromAccount, BankAccountStub.buildEntity());

        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    void getOrder_DeveRetornar3() {
        assertEquals(3, validator.getOrder());
    }
}