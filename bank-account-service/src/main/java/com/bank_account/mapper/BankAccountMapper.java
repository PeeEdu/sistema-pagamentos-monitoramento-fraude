package com.bank_account.mapper;

import com.bank_account.dto.request.CreateAccountRequest;
import com.bank_account.dto.response.BankAccountResponse;
import com.bank_account.entities.BankAccount;

public class BankAccountMapper {
    public static BankAccount toEntity(final CreateAccountRequest account){
        return BankAccount.builder()
                .agency(account.agency())
                .accountNumber(account.number())
                .owner(account.owner())
                .type(account.type())
                .build();
    }

    public static BankAccountResponse toResponse(final BankAccount bankAccount){
        return BankAccountResponse.builder()
                .id(bankAccount.getId())
                .agency(bankAccount.getAgency())
                .accountNumber(bankAccount.getAccountNumber())
                .owner(bankAccount.getOwner())
                .type(bankAccount.getType())
                .balance(bankAccount.getBalance())
                .createdAt(bankAccount.getCreatedAt())
                .updatedAt(bankAccount.getUpdatedAt())
                .build();
    }
}
