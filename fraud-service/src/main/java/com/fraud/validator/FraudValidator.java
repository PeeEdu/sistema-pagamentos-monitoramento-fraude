package com.fraud.validator;

import com.fraud.entity.FraudAnalysisResult;
import com.fraud.event.TransferInitiatedEvent;

public interface FraudValidator {
    FraudAnalysisResult validate(TransferInitiatedEvent event);
}