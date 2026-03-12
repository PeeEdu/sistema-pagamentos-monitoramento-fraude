package com.fraud.mapper;

import com.fraud.entity.FraudEntity;
import com.fraud.enums.FraudStatus;
import com.fraud.enums.FraudType;
import com.fraud.event.TransferInitiatedEvent;
import com.fraud.event.TransferValidatedEvent;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(
        componentModel = "spring",
        imports = {LocalDateTime.class, FraudStatus.class}
)
public interface FraudMapper {

    @Mapping(target = "transferId", source = "event.transferId")
    @Mapping(target = "fromAccountNumber", source = "event.fromAccountNumber")
    @Mapping(target = "pixKey", source = "event.pixKey")
    @Mapping(target = "amount", source = "event.amount")
    @Mapping(target = "description", source = "event.description")
    @Mapping(target = "initiatedBy", source = "event.initiatedBy")
    @Mapping(target = "approved", source = "approved")
    @Mapping(target = "riskScore", source = "riskScore")
    @Mapping(target = "fraudTypes", source = "fraudTypes")
    @Mapping(target = "rejectionReason", expression = "java(buildRejectionReason(approved, fraudTypes))")
    @Mapping(target = "validatedAt", expression = "java(java.time.LocalDateTime.now())")
    TransferValidatedEvent toValidatedEvent(
            TransferInitiatedEvent event,
            boolean approved,
            double riskScore,
            List<FraudType> fraudTypes
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactionId", source = "event.transferId")
    @Mapping(target = "userId", source = "event.initiatedBy")
    @Mapping(target = "amount", source = "event.amount")
    @Mapping(target = "fraudTypes", source = "fraudTypes")
    @Mapping(target = "status", constant = "DETECTED")
    @Mapping(target = "riskLevel", ignore = true)
    @Mapping(target = "riskScore", source = "riskScore")
    @Mapping(target = "detectedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "detectionMethod", constant = "RULE_BASED")
    @Mapping(target = "triggeredRules", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    @Mapping(target = "ipAddress", ignore = true)
    @Mapping(target = "deviceId", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    @Mapping(target = "observation", ignore = true)
    FraudEntity toFraudEntity(
            TransferInitiatedEvent event,
            double riskScore,
            List<FraudType> fraudTypes
    );

    default String buildRejectionReason(boolean approved, List<FraudType> fraudTypes) {
        if (approved) {
            return null;
        }
        return "Fraude detectada: " + fraudTypes;
    }
}