package com.bank_account.validator;

import com.bank_account.dto.response.ValidationResult;
import com.bank_account.entity.BankAccount;
import com.bank_account.event.TransferValidatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ToAccountActiveValidator implements TransferValidator {

    @Override
    public ValidationResult validate(TransferValidatedEvent event, BankAccount fromAccount, BankAccount toAccount) {
        if (!"ACTIVE".equals(toAccount.getStatus())) {
            log.error("❌ Conta de destino não está ativa");
            return ValidationResult.failure("Conta de destino não está ativa");
        }
        return ValidationResult.success();
    }

    @Override
    public int getOrder() {
        return 5;
    }
}