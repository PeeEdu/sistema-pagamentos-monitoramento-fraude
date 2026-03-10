package com.bank_account.service;

import com.bank_account.dto.request.CreatePixKeyRequest;
import com.bank_account.dto.response.BankAccountResponse;
import com.bank_account.dto.response.CreatePixKeyResponse;
import com.bank_account.entity.BankAccount;
import com.bank_account.entity.PixKey;
import com.bank_account.event.TransferInitiatedEvent;
import com.bank_account.event.UserCreatedEvent;
import com.bank_account.mapper.BankAccountMapper;
import com.bank_account.repository.BankAccountRepository;
import com.bank_account.validator.PixKeyValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapper bankAccountMapper;
    private final PixKeyValidator pixKeyValidator;

    public BankAccountResponse getBankAccountByUserId(String userId){
        BankAccount bankAccount = bankAccountRepository.findByUserId(userId);



        return bankAccountMapper.toResponse(bankAccount);
    }

    public void createAccountForUser(UserCreatedEvent event) {
        log.info("🏦 Criando conta bancária para userId: {}", event.getUserId());

         var bankAccountEntity = bankAccountMapper.toEntity(event);
         bankAccountEntity.setAccountNumber(generateAccountNumber());

        BankAccount savedAccount = bankAccountRepository.save(bankAccountEntity);
        log.info("✅ Conta criada: {} para userId: {}",
                savedAccount.getAccountNumber(),
                savedAccount.getUserId());

//        return savedAccount;
    }

    @Transactional
    public CreatePixKeyResponse createPixKey(String userId, CreatePixKeyRequest createPixKeyRequest) {
        log.info("🔑 Criando chave PIX para usuário: {}", userId);

        BankAccount bankAccount = bankAccountRepository.findByUserId(userId);

        pixKeyValidator.validateBankAccountExists(bankAccount, userId);
        pixKeyValidator.validatePixKeyNotExists(bankAccount, createPixKeyRequest);
        pixKeyValidator.initializePixKeyListIfNeeded(bankAccount);

        PixKey pixKey = bankAccountMapper.toPixKeyEntity(createPixKeyRequest);
        bankAccount.getPixKey().add(pixKey);

        bankAccountRepository.save(bankAccount);

        log.info("✅ Chave PIX criada: {} - {}", pixKey.getType(), pixKey.getKey());

        return bankAccountMapper.toPixKeyResponse(pixKey, userId);
    }

    public void process(TransferInitiatedEvent event){
        log.info("evento aqui: {}", event);
    }

    private String generateAccountNumber() {
        return String.format("%08d-%d",
                new Random().nextInt(99999999),
                new Random().nextInt(10));
    }
}