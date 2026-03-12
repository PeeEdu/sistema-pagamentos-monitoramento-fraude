package com.bank_account.validator;

import com.bank_account.dto.response.ValidationResult;
import com.bank_account.entity.BankAccount;
import com.bank_account.event.TransferValidatedEvent;

public interface TransferValidator {
    ValidationResult validate(TransferValidatedEvent event, BankAccount fromAccount, BankAccount toAccount);


    default int getOrder() {
        return 100;
    }
}