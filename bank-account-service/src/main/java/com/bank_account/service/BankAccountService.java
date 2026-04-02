package com.bank_account.service;

import com.bank_account.dto.request.CreatePixKeyRequest;
import com.bank_account.dto.request.DepositRequest;
import com.bank_account.dto.response.BankAccountResponse;
import com.bank_account.dto.response.CreatePixKeyResponse;
import com.bank_account.dto.response.PixKeysListResponse;
import com.bank_account.entity.BankAccount;
import com.bank_account.entity.PixKey;
import com.bank_account.event.UserCreatedEvent;
import com.bank_account.mapper.BankAccountMapper;
import com.bank_account.producer.PixKeyEventProducer;
import com.bank_account.repository.BankAccountRepository;
import com.bank_account.validator.PixKeyValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapper bankAccountMapper;
    private final PixKeyValidator pixKeyValidator;
    private final PixKeyEventProducer pixKeyEventProducer;

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

        try {
            pixKeyEventProducer.sendPixKeyCreatedEvent(bankAccount, pixKey);
        } catch (Exception e) {
            log.error("❌ Erro ao enviar evento, mas operação continua: {}", e.getMessage());
        }

        return bankAccountMapper.toPixKeyResponse(pixKey, userId);
    }

    @Transactional
    public void deletePixKey(String accountNumber, String pixKeyToDelete) {
        log.info("🗑️ Removendo chave PIX: {} da conta: {}", pixKeyToDelete, accountNumber);

        BankAccount bankAccount = bankAccountRepository.findByAccountNumber(accountNumber);

        if (bankAccount == null) {
            throw new RuntimeException("Conta bancária não encontrada: " + accountNumber);
        }

        if (bankAccount.getPixKey() == null || bankAccount.getPixKey().isEmpty()) {
            throw new RuntimeException("Nenhuma chave PIX encontrada para a conta: " + accountNumber);
        }

        boolean removed = bankAccount.getPixKey().removeIf(pixKey ->
                pixKey.getKey().equals(pixKeyToDelete)
        );

        if (!removed) {
            throw new RuntimeException("Chave PIX não encontrada: " + pixKeyToDelete);
        }

        bankAccountRepository.save(bankAccount);

        log.info("✅ Chave PIX removida: {} da conta: {}", pixKeyToDelete, accountNumber);

        try {
            pixKeyEventProducer.sendPixKeyDeletedEvent(bankAccount, pixKeyToDelete);
        } catch (Exception e) {
            log.error("❌ Erro ao enviar evento de exclusão, mas operação continua: {}", e.getMessage());
        }
    }


    public BankAccountResponse deposit(String accountNumber, DepositRequest depositRequest){
        var bankAccount = bankAccountRepository.findByAccountNumber(accountNumber);

        bankAccount.deposit(depositRequest.balance());
        bankAccountRepository.save(bankAccount);

        return bankAccountMapper.toResponse(bankAccount);
    }

    public PixKeysListResponse getPixKeysByAccountNumber(String accountNumber) {
        log.info("🔍 Buscando chaves PIX para conta: {}", accountNumber);

        BankAccount bankAccount = bankAccountRepository.findByAccountNumber(accountNumber);

        if (bankAccount == null) {
            throw new RuntimeException("Conta bancária não encontrada: " + accountNumber);
        }

        PixKeysListResponse response = bankAccountMapper.toPixKeysListResponse(bankAccount);

        log.info("✅ Encontradas {} chaves PIX para conta: {}", response.totalKeys(), accountNumber);

        return response;
    }

    private String generateAccountNumber() {
        return String.format("%08d-%d",
                new Random().nextInt(99999999),
                new Random().nextInt(10));
    }
}