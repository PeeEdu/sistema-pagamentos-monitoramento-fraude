package com.bank_account.validator;

import com.bank_account.dto.response.ValidationResult;
import com.bank_account.entity.BankAccount;
import com.bank_account.event.TransferValidatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FromAccountActiveValidator implements TransferValidator {

    @Override
    public ValidationResult validate(TransferValidatedEvent event, BankAccount fromAccount, BankAccount toAccount) {
        if (!"ACTIVE".equals(fromAccount.getStatus())) {
            log.error("❌ Conta de origem não está ativa");
            return ValidationResult.failure("Conta de origem não está ativa");
        }
        return ValidationResult.success();
    }

    @Override
    public int getOrder() {
        return 4;
    }
}