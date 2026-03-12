package com.bank_account.validator;

import com.bank_account.dto.response.ValidationResult;
import com.bank_account.entity.BankAccount;
import com.bank_account.event.TransferValidatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ToAccountExistsValidator implements TransferValidator {

    @Override
    public ValidationResult validate(TransferValidatedEvent event, BankAccount fromAccount, BankAccount toAccount) {
        if (toAccount == null) {
            log.error("❌ Chave PIX não encontrada: {}", event.getPixKey());
            return ValidationResult.failure("Chave PIX não encontrada");
        }
        return ValidationResult.success();
    }

    @Override
    public int getOrder() {
        return 2;
    }
}