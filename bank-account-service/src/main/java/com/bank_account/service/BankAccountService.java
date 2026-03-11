package com.bank_account.service;

import com.bank_account.dto.request.CreatePixKeyRequest;
import com.bank_account.dto.request.DepositRequest;
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

    public BankAccountResponse deposit(String accountNumber, DepositRequest depositRequest){
        var bankAccount = bankAccountRepository.findByAccountNumber(accountNumber);

        bankAccount.deposit(depositRequest.balance());
        bankAccountRepository.save(bankAccount);

        return bankAccountMapper.toResponse(bankAccount);
    }

    @Transactional
    public void process(TransferInitiatedEvent event) {
        log.info("🔍 Processando transferência: {}", event.getTransferId());
        BankAccount accountDecrease = bankAccountRepository.findByAccountNumber(event.getFromAccountNumber());

        if (accountDecrease == null) {
            log.error("Conta de origem não encontrada: {}", event.getFromAccountNumber());
//            sendFailure(event, "Conta de origem não encontrada");
            return;
        }

        if (accountDecrease.getBalance().compareTo(event.getAmount()) < 0) {
            log.error("Saldo insuficiente para a transação");

            return;
        }

        // ✅ Busca conta de destino (crédito) pela chave PIX
        BankAccount accountIncrease = bankAccountRepository.findByPixKeyKey(event.getPixKey())
                .orElse(null);

        if (accountIncrease == null) {
            log.error("Chave PIX não encontrada: {}", event.getPixKey());
//            sendFailure(event, "Chave PIX não encontrada");
            return;
        }

        // ✅ Valida saldo
        if (accountDecrease.getBalance().compareTo(event.getAmount()) < 0) {
            log.error("Saldo insuficiente. Saldo: {}, Valor: {}",
                    accountDecrease.getBalance(), event.getAmount());
//            sendFailure(event, "Saldo insuficiente");
            return;
        }

        // ✅ Valida status da conta de origem
        if (!"ACTIVE".equals(accountDecrease.getStatus())) {
            log.error("Conta de origem não está ativa");
//            sendFailure(event, "Conta não está ativa");
            return;
        }

        // ✅ Valida status da conta de destino
        if (!"ACTIVE".equals(accountIncrease.getStatus())) {
            log.error("Conta de destino não está ativa");
//            sendFailure(event, "Conta de destino não está ativa");
            return;
        }

        accountDecrease.withdraw(event.getAmount());
        accountIncrease.deposit(event.getAmount());

        bankAccountRepository.save(accountDecrease);
        bankAccountRepository.save(accountIncrease);

        log.info("✅ Transferência realizada com sucesso");
        log.info("   Conta origem - Novo saldo: {}", accountDecrease);
        log.info("   Conta destino - Novo saldo: {}", accountIncrease);

        // ✅ Envia evento de sucesso
//        sendSuccess(event, accountIncrease.getAccountNumber());
    }

    private String generateAccountNumber() {
        return String.format("%08d-%d",
                new Random().nextInt(99999999),
                new Random().nextInt(10));
    }
}