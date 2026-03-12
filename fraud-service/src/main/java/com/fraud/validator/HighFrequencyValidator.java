package com.fraud.validator;

import com.fraud.entity.FraudAnalysisResult;
import com.fraud.enums.FraudType;
import com.fraud.event.TransferInitiatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class HighFrequencyValidator implements FraudValidator {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final int MAX_TRANSACTIONS_PER_MINUTE = 5;

    @Override
    public FraudAnalysisResult validate(TransferInitiatedEvent event) {
        List<FraudType> fraudTypes = new ArrayList<>();
        double riskScore = 0.0;

        String key = "transfer:count:" + event.getFromAccountNumber();

        Long count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            redisTemplate.expire(key, 1, TimeUnit.MINUTES);
        }

        if (count > MAX_TRANSACTIONS_PER_MINUTE) {
            log.warn("⚠️ Alta frequência detectada: {} transações em 1 minuto", count);
            fraudTypes.add(FraudType.HIGH_FREQUENCY);
            riskScore = 30.0;
        }

        return FraudAnalysisResult.builder()
                .transferId(event.getTransferId())
                .isFraud(!fraudTypes.isEmpty())
                .fraudTypes(fraudTypes)
                .riskScore(riskScore)
                .build();
    }
}