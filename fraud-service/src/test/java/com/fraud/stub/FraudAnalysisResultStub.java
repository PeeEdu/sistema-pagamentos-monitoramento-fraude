package com.fraud.stub;

import com.fraud.entity.FraudAnalysisResult;
import com.fraud.enums.FraudType;

import java.util.List;

public class FraudAnalysisResultStub {

    private FraudAnalysisResultStub() {
    }

    public static FraudAnalysisResult buildEntity() {
        return FraudAnalysisResult.builder()
                .transferId("transfer-123")
                .isFraud(true)
                .fraudTypes(List.of(FraudType.HIGH_FREQUENCY))
                .riskScore(85.5)
                .build();
    }

    public static FraudAnalysisResult buildApprovedEntity() {
        return FraudAnalysisResult.builder()
                .transferId("transfer-123")
                .isFraud(false)
                .fraudTypes(List.of())
                .riskScore(10.0)
                .build();
    }

    public static FraudAnalysisResult buildFraudEntity() {
        return FraudAnalysisResult.builder()
                .transferId("transfer-123")
                .isFraud(true)
                .fraudTypes(List.of(FraudType.HIGH_FREQUENCY, FraudType.HIGH_VALUE))
                .riskScore(95.0)
                .build();
    }
}