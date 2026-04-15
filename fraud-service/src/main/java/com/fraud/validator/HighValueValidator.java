package com.fraud.validator;

import com.fraud.entity.FraudAnalysisResult;
import com.fraud.enums.FraudType;
import com.fraud.event.TransferInitiatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class HighValueValidator implements FraudValidator {

    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("5000");

    @Override
    public FraudAnalysisResult validate(TransferInitiatedEvent event) {
        List<FraudType> fraudTypes = new ArrayList<>();
        double riskScore = 0.0;

        if (event.getAmount().compareTo(HIGH_VALUE_THRESHOLD) > 0) {
            log.warn("⚠️ Valor alto detectado: {}", event.getAmount());
            fraudTypes.add(FraudType.HIGH_VALUE);
            riskScore = 40.0;
        }

        if (event.getAmount().compareTo(new BigDecimal(20000)) > 0) {
            log.warn("⚠️ Valor Exorbitante detectado: {}", event.getAmount());
            fraudTypes.add(FraudType.HIGH_VALUE);
            riskScore = 60.0;
        }

        return FraudAnalysisResult.builder()
                .transferId(event.getTransferId())
                .isFraud(!fraudTypes.isEmpty())
                .fraudTypes(fraudTypes)
                .riskScore(riskScore)
                .build();
    }
}
