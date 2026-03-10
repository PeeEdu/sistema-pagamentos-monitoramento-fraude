package com.bank_account.mapper;

import com.bank_account.dto.request.CreatePixKeyRequest;
import com.bank_account.dto.response.BankAccountResponse;
import com.bank_account.dto.response.CreatePixKeyResponse;
import com.bank_account.entity.BankAccount;
import com.bank_account.entity.PixKey;
import com.bank_account.event.UserCreatedEvent;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.time.Instant;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BankAccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "balance", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "accountType", constant = "CORRENTE")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "currency", constant = "BRL")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", ignore = true)
    BankAccount toEntity(UserCreatedEvent event);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "accountType", source = "accountType")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "createdAt", source = "createdAt")
    BankAccountResponse toResponse(BankAccount bankAccount);


    PixKey toPixKeyEntity(CreatePixKeyRequest createPixKeyRequest);

    // ✅ PixKey + userId -> CreatePixKeyResponse
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "type", source = "pixKey.type")
    @Mapping(target = "key", source = "pixKey.key")
    CreatePixKeyResponse toPixKeyResponse(PixKey pixKey, String userId);
}

