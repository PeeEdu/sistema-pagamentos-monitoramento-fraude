package com.fraud.entity;

import com.fraud.enums.FraudType;
import com.fraud.enums.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudAnalysisResult {
    private String transferId;
    private boolean isFraud;
    private List<FraudType> fraudTypes;
    private double riskScore;
}