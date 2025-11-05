package com.bank_account.service;

import com.bank_account.dto.request.CreateAccountRequest;
import com.bank_account.dto.response.BankAccountResponse;
import com.bank_account.entities.BankAccount;
import com.bank_account.mapper.BankAccountMapper;
import com.bank_account.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public record BankAccountService (
        BankAccountRepository bankAccountRepository
) {

    public BankAccountResponse createBankAccount(CreateAccountRequest account) {
        if (Objects.isNull(account)) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        BankAccount bankAccount = BankAccountMapper.toEntity(account);
        bankAccountRepository.save(bankAccount);


        return BankAccountMapper.toResponse(bankAccount);
    }

}
