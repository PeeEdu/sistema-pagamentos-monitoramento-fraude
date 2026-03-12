package com.fraud.enums;

public enum RiskLevel {
    LOW,        // Risco baixo (0-30%)
    MEDIUM,     // Risco médio (30-60%)
    HIGH,       // Risco alto (60-80%)
    CRITICAL;    // Risco crítico (80-100%)

    public static RiskLevel fromScore(double riskScore) {
        if (riskScore < 30.0) {
            return LOW;
        } else if (riskScore < 60.0) {
            return MEDIUM;
        } else if (riskScore < 80.0) {
            return HIGH;
        } else {
            return CRITICAL;
        }
    }
}