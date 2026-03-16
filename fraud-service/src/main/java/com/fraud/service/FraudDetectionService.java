package com.fraud.service;

import com.fraud.entity.FraudAnalysisResult;
import com.fraud.entity.FraudEntity;
import com.fraud.enums.FraudStatus;
import com.fraud.enums.FraudType;
import com.fraud.enums.RiskLevel;
import com.fraud.event.TransferInitiatedEvent;
import com.fraud.event.TransferValidatedEvent;
import com.fraud.mapper.FraudMapper;
import com.fraud.producer.TransferValidatedProducer;
import com.fraud.repository.FraudRepository;
import com.fraud.validator.FraudValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final List<FraudValidator> validators;
    private final FraudRepository fraudRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TransferValidatedProducer transferValidatedProducer;
    private final FraudMapper fraudMapper;

    public void analyze(TransferInitiatedEvent event) {
        log.info("🔍 Analisando transferência: {}", event.getTransferId());

        List<FraudType> allFraudTypes = new ArrayList<>();
        double totalRiskScore = 0.0;

        for (FraudValidator validator : validators) {
            FraudAnalysisResult result = validator.validate(event);

            if (result.isFraud()) {
                allFraudTypes.addAll(result.getFraudTypes());
                totalRiskScore += result.getRiskScore();
            }
        }

        RiskLevel finalRiskLevel = RiskLevel.fromScore(totalRiskScore);
        
        boolean approved = shouldApprove(finalRiskLevel, totalRiskScore);

        saveTransactionToRedis(event);

        if (!approved) {
            FraudEntity fraudEntity = fraudMapper.toFraudEntity(event, totalRiskScore, allFraudTypes);
            fraudEntity.setRiskLevel(finalRiskLevel);
            fraudEntity.setStatus(FraudStatus.BLOCKED);
            fraudRepository.save(fraudEntity);
        }

        log.info("📊 Análise completa:");
        log.info("   Aprovado: {}", approved);
        log.info("   Score: {}", totalRiskScore);
        log.info("   Risk Level: {}", finalRiskLevel);
        log.info("   Tipos: {}", allFraudTypes);

        TransferValidatedEvent validatedEvent = fraudMapper.toValidatedEvent(
                event,
                approved,
                totalRiskScore,
                allFraudTypes
        );

        transferValidatedProducer.send(validatedEvent);
    }

    private boolean shouldApprove(RiskLevel riskLevel, double score) {
        return switch (riskLevel) {
            case LOW -> true;

            case MEDIUM -> score < 50.0;

            case HIGH -> false;

            case CRITICAL -> false;

            default -> false;
        };
    }

    private void saveTransactionToRedis(TransferInitiatedEvent event) {
        String key = "transfer:history:" + event.getFromAccountNumber();

        redisTemplate.opsForList().leftPush(key, event);
        redisTemplate.opsForList().trim(key, 0, 9);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
    }
}
