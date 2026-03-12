package com.bank_account.service;

import com.bank_account.dto.response.ValidationResult;
import com.bank_account.entity.BankAccount;
import com.bank_account.event.TransferValidatedEvent;
import com.bank_account.validator.TransferValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferValidationService {

    private final List<TransferValidator> validators;
    public ValidationResult validateAll(TransferValidatedEvent event,
                                        BankAccount fromAccount,
                                        BankAccount toAccount) {

        log.info("🔍 Iniciando validações da transferência: {}", event.getTransferId());

        return validators.stream()
                .sorted(Comparator.comparingInt(TransferValidator::getOrder))
                .map(validator -> {
                    log.debug("Executando: {}", validator.getClass().getSimpleName());
                    return validator.validate(event, fromAccount, toAccount);
                })
                .filter(result -> !result.isValid())
                .findFirst()
                .orElse(ValidationResult.success());
    }
}