package com.bank_account.service;

import com.bank_account.dto.response.BankAccountResponse;
import com.bank_account.entity.BankAccount;
import com.bank_account.event.UserCreatedEvent;
import com.bank_account.mapper.BankAccountMapper;
import com.bank_account.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapper bankAccountMapper;

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

    private String generateAccountNumber() {
        return String.format("%08d-%d",
                new Random().nextInt(99999999),
                new Random().nextInt(10));
    }
}