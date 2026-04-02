package com.fraud.stub;

import com.fraud.entity.FraudEntity;
import com.fraud.enums.FraudStatus;
import com.fraud.enums.FraudType;
import com.fraud.enums.RiskLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class FraudEntityStub {

    private FraudEntityStub() {
    }

    public static FraudEntity buildEntity() {
        return FraudEntity.builder()
                .id("fraud-123")
                .transactionId("transfer-123")
                .amount(new BigDecimal("1000.00"))
                .fraudTypes(List.of(FraudType.HIGH_FREQUENCY))
                .status(FraudStatus.CONFIRMED)
                .riskLevel(RiskLevel.HIGH)
                .riskScore(85.5)
                .detectedAt(LocalDateTime.of(2024, 1, 1, 10, 0, 0))
                .updatedAt(LocalDateTime.of(2024, 1, 1, 10, 5, 0))
                .detectionMethod("RULE_ENGINE")
                .triggeredRules(List.of("LOCATION_MISMATCH"))
                .metadata(Map.of("source", "unit-test"))
                .ipAddress("192.168.0.1")
                .deviceId("device-123")
                .location("São Paulo")
                .reviewedBy("analyst-1")
                .reviewedAt(LocalDateTime.of(2024, 1, 1, 11, 0, 0))
                .observation("Possível fraude detectada")
                .build();
    }

    public static FraudEntity buildEntityWithStatus(FraudStatus status) {
        return FraudEntity.builder()
                .id("fraud-123")
                .transactionId("transfer-123")
                .amount(new BigDecimal("1000.00"))
                .fraudTypes(List.of(FraudType.HIGH_FREQUENCY))
                .status(status)
                .riskLevel(RiskLevel.HIGH)
                .riskScore(85.5)
                .detectedAt(LocalDateTime.of(2024, 1, 1, 10, 0, 0))
                .updatedAt(LocalDateTime.of(2024, 1, 1, 10, 5, 0))
                .detectionMethod("RULE_ENGINE")
                .triggeredRules(List.of("LOCATION_MISMATCH"))
                .metadata(Map.of("source", "unit-test"))
                .ipAddress("192.168.0.1")
                .deviceId("device-123")
                .location("São Paulo")
                .reviewedBy("analyst-1")
                .reviewedAt(LocalDateTime.of(2024, 1, 1, 11, 0, 0))
                .observation("Possível fraude detectada")
                .build();
    }

    public static FraudEntity buildEntityWithoutReview() {
        return FraudEntity.builder()
                .id("fraud-123")
                .transactionId("transfer-123")
                .amount(new BigDecimal("1000.00"))
                .fraudTypes(List.of(FraudType.HIGH_FREQUENCY))
                .status(FraudStatus.CONFIRMED)
                .riskLevel(RiskLevel.HIGH)
                .riskScore(85.5)
                .detectedAt(LocalDateTime.of(2024, 1, 1, 10, 0, 0))
                .updatedAt(LocalDateTime.of(2024, 1, 1, 10, 5, 0))
                .detectionMethod("RULE_ENGINE")
                .triggeredRules(List.of("LOCATION_MISMATCH"))
                .metadata(Map.of("source", "unit-test"))
                .ipAddress("192.168.0.1")
                .deviceId("device-123")
                .location("São Paulo")
                .reviewedBy(null)
                .reviewedAt(null)
                .observation(null)
                .build();
    }
}