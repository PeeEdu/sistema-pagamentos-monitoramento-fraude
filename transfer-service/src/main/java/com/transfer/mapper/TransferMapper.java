package com.transfer.mapper;

import com.transfer.dto.request.CreatePixTransferRequest;
import com.transfer.dto.response.PixResponse;
import com.transfer.entity.PixTransfer;
import com.transfer.entity.Transfer;
import com.transfer.event.TransferInitiatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransferMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fromAccountNumber", source = "fromAccountNumber")
    @Mapping(target = "pixKey", source = "pix.key")
    @Mapping(target = "pixKeyType", source = "pix.type")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "status", constant = "PENDING") // ou ignore = true
    @Mapping(target = "failureReason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "initiatedBy", source = "initiatedBy")
    @Mapping(target = "geoLocalization", source = "geoLocalization")
    PixTransfer toEntity(CreatePixTransferRequest request);

    @Mapping(target = "pixKey", source = "pixKey")
    @Mapping(target = "pixKeyType", source = "pixKeyType")
    @Mapping(target = "geoLocalization", source = "geoLocalization")
    PixResponse toResponse(PixTransfer transferEntity);

    @Mapping(target = "transferId", source = "id")
    @Mapping(target = "fromAccountNumber", source = "fromAccountNumber")
    @Mapping(target = "pixKey", source = "pixKey")
    @Mapping(target = "pixKeyType", source = "pixKeyType")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "initiatedBy", source = "initiatedBy")
    @Mapping(target = "geoLocalization", source = "geoLocalization")
    @Mapping(target = "initiatedAt", expression = "java(java.time.LocalDateTime.now())")
    TransferInitiatedEvent toEvent(PixTransfer transfer);
}