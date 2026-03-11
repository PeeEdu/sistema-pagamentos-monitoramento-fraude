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
public class DuplicateTransactionValidator implements FraudValidator {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public FraudAnalysisResult validate(TransferInitiatedEvent event) {
        List<FraudType> fraudTypes = new ArrayList<>();
        double riskScore = 0.0;

        String key = "transfer:duplicate:" + event.getFromAccountNumber() + ":" +
                event.getPixKey() + ":" + event.getAmount();

        Boolean exists = redisTemplate.hasKey(key);

        if (Boolean.TRUE.equals(exists)) {
            log.warn("⚠️ Duplicação detectada: {}", event.getTransferId());
            fraudTypes.add(FraudType.DUPLICATE_TRANSACTION);
            riskScore = 40.0;
        } else {
            // Marca como processada por 5 minutos
            redisTemplate.opsForValue().set(key, "1", 5, TimeUnit.MINUTES);
        }

        return FraudAnalysisResult.builder()
                .transferId(event.getTransferId())
                .isFraud(!fraudTypes.isEmpty())
                .fraudTypes(fraudTypes)
                .riskScore(riskScore)
                .build();
    }
}