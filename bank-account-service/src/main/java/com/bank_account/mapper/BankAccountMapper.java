package com.bank_account.mapper;

import com.bank_account.dto.request.CreatePixKeyRequest;
import com.bank_account.dto.response.BankAccountResponse;
import com.bank_account.dto.response.CreatePixKeyResponse;
import com.bank_account.dto.response.PixKeyResponse;
import com.bank_account.dto.response.PixKeysListResponse;
import com.bank_account.entity.BankAccount;
import com.bank_account.entity.PixKey;
import com.bank_account.event.PixKeyCreatedEvent;
import com.bank_account.event.PixKeyDeletedEvent;
import com.bank_account.event.UserCreatedEvent;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

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

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "type", source = "pixKey.type")
    @Mapping(target = "key", source = "pixKey.key")
    CreatePixKeyResponse toPixKeyResponse(PixKey pixKey, String userId);

    @Mapping(target = "key", source = "key")
    @Mapping(target = "type", source = "type")
    PixKeyResponse toPixKeyResponse(PixKey pixKey);

    List<PixKeyResponse> toPixKeyResponseList(List<PixKey> pixKeys);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "pixKeys", source = "pixKey")
    @Mapping(target = "totalKeys", expression = "java(bankAccount.getPixKey() != null ? bankAccount.getPixKey().size() : 0)")
    PixKeysListResponse toPixKeysListResponse(BankAccount bankAccount);

    default List<String> toPixKeyStringList(List<PixKey> pixKeys) {
        if (pixKeys == null) {
            return Collections.emptyList();
        }
        return pixKeys.stream()
                .map(PixKey::getKey)
                .toList();
    }

    @Mapping(target = "userId", source = "bankAccount.userId")
    @Mapping(target = "accountNumber", source = "bankAccount.accountNumber")
    @Mapping(target = "pixKey", source = "pixKey.key")
    @Mapping(target = "pixKeyType", expression = "java(pixKey.getType().name())")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    PixKeyCreatedEvent toPixKeyCreatedEvent(BankAccount bankAccount, PixKey pixKey);

    @Mapping(target = "userId", source = "bankAccount.userId")
    @Mapping(target = "accountNumber", source = "bankAccount.accountNumber")
    @Mapping(target = "pixKey", source = "pixKey")
    @Mapping(target = "deletedAt", expression = "java(java.time.Instant.now())")
    PixKeyDeletedEvent toPixKeyDeletedEvent(BankAccount bankAccount, String pixKey);
}