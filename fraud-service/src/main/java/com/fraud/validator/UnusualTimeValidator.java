package com.fraud.validator;

import com.fraud.entity.FraudAnalysisResult;
import com.fraud.enums.FraudType;
import com.fraud.event.TransferInitiatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UnusualTimeValidator implements FraudValidator {

    private static final LocalTime NIGHT_START = LocalTime.of(23, 0);
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);

    @Override
    public FraudAnalysisResult validate(TransferInitiatedEvent event) {
        List<FraudType> fraudTypes = new ArrayList<>();
        double riskScore = 0.0;

        LocalTime now = LocalTime.now();

        if (now.isAfter(NIGHT_START) || now.isBefore(NIGHT_END)) {
            log.warn("⚠️ Horário suspeito detectado: {}", now);
            fraudTypes.add(FraudType.UNUSUAL_TIME);
            riskScore = 15.0;
        }

        return FraudAnalysisResult.builder()
                .transferId(event.getTransferId())
                .isFraud(!fraudTypes.isEmpty())
                .fraudTypes(fraudTypes)
                .riskScore(riskScore)
                .build();
    }
}