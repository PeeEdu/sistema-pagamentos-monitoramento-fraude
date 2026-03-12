package com.bank_account.validator;

import com.bank_account.dto.response.ValidationResult;
import com.bank_account.entity.BankAccount;
import com.bank_account.event.TransferValidatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SufficientBalanceValidator implements TransferValidator {

    @Override
    public ValidationResult validate(TransferValidatedEvent event, BankAccount fromAccount, BankAccount toAccount) {
        if (fromAccount.getBalance().compareTo(event.getAmount()) < 0) {
            log.error("❌ Saldo insuficiente. Saldo: {}, Valor: {}",
                    fromAccount.getBalance(), event.getAmount());
            return ValidationResult.failure("Saldo insuficiente");
        }
        return ValidationResult.success();
    }

    @Override
    public int getOrder() {
        return 3;
    }
}