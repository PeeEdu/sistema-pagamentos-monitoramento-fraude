package com.bank_account.service;

import com.bank_account.entity.BankAccount;
import com.bank_account.event.TransferValidatedEvent;
import com.bank_account.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferProcessingService {

    private final BankAccountRepository bankAccountRepository;

    @Transactional
    public void process(TransferValidatedEvent event) {
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

    public void reject(TransferValidatedEvent event){

    }
}
