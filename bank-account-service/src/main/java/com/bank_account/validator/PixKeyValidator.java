package com.bank_account.validator;

import com.bank_account.dto.request.CreatePixKeyRequest;
import com.bank_account.entity.BankAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Component
public class PixKeyValidator {

    public void validateBankAccountExists(BankAccount bankAccount, String userId) {
        if (bankAccount == null) {
            log.error("Conta bancária não encontrada para o usuário: {}", userId);
            throw new RuntimeException("Conta bancária não encontrada");
        }
    }

    public void validatePixKeyNotExists(BankAccount bankAccount, CreatePixKeyRequest request) {
        if (bankAccount.getPixKey() != null) {
            boolean exists = bankAccount.getPixKey().stream()
                    .anyMatch(pk -> pk.getKey().equals(request.key()));

            if (exists) {
                log.error("Chave PIX já cadastrada: {} - {}", request.type(), request.key());
                throw new RuntimeException("Chave PIX já cadastrada");
            }
        }
    }

    public void initializePixKeyListIfNeeded(BankAccount bankAccount) {
        if (bankAccount.getPixKey() == null) {
            bankAccount.setPixKey(new ArrayList<>());
        }
    }
}