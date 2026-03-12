package com.bank_account.mapper;

import com.bank_account.event.TransferCompletedEvent;
import com.bank_account.event.TransferValidatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface TransferEventMapper {

    @Mapping(target = "transferId", source = "transferId")
    @Mapping(target = "fromAccountNumber", source = "fromAccountNumber")
    @Mapping(target = "toAccountId", source = "pixKey")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "initiatedBy", source = "initiatedBy")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "failureReason", ignore = true)
    @Mapping(target = "geoLocalization", constant = "N/A")
    @Mapping(target = "completedAt", expression = "java(java.time.LocalDateTime.now())")
    TransferCompletedEvent toCompletedEvent(TransferValidatedEvent validated);
}