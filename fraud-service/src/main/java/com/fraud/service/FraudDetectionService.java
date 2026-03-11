package com.fraud.service;

import com.fraud.entity.FraudAnalysisResult;
import com.fraud.entity.FraudEntity;
import com.fraud.enums.FraudStatus;
import com.fraud.enums.FraudType;
import com.fraud.event.TransferInitiatedEvent;
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

    private final List<FraudValidator> validators;  // ✅ Spring injeta todos automaticamente
    private final FraudRepository fraudRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public FraudAnalysisResult analyze(TransferInitiatedEvent event) {
        log.info("🔍 Analisando transferência: {}", event.getTransferId());

        List<FraudType> allFraudTypes = new ArrayList<>();
        double totalRiskScore = 0.0;

        // ✅ Executa todos os validators
        for (FraudValidator validator : validators) {
            FraudAnalysisResult result = validator.validate(event);

            if (result.isFraud()) {
                allFraudTypes.addAll(result.getFraudTypes());
                totalRiskScore += result.getRiskScore();
            }
        }

        boolean isFraud = totalRiskScore >= 50.0;
        saveTransactionToRedis(event);
        if (isFraud) {
            saveFraudToMongo(event, allFraudTypes, totalRiskScore);
        }

        log.info("📊 Análise concluída - Fraude: {} - Score: {} - Tipos: {}",
                isFraud, totalRiskScore, allFraudTypes);

        return FraudAnalysisResult.builder()
                .transferId(event.getTransferId())
                .isFraud(isFraud)
                .fraudTypes(allFraudTypes)
                .riskScore(totalRiskScore)
                .build();
    }

    private void saveTransactionToRedis(TransferInitiatedEvent event) {
        String key = "transfer:history:" + event.getFromAccountNumber();

        redisTemplate.opsForList().leftPush(key, event);
        redisTemplate.opsForList().trim(key, 0, 9);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
    }

    private void saveFraudToMongo(TransferInitiatedEvent event,
                                  List<FraudType> fraudTypes,
                                  double riskScore) {

        log.error("🚨 Salvando fraude no MongoDB: {}", event.getTransferId());

        FraudEntity fraud = FraudEntity.builder()
                .transactionId(event.getTransferId())
                .userId(event.getInitiatedBy())
                .amount(event.getAmount())
                .fraudType(fraudTypes.get(0))
                .status(FraudStatus.DETECTED)
                .riskScore(riskScore)
                .detectedAt(LocalDateTime.now())
                .detectionMethod("RULE_BASED")
                .build();

        fraudRepository.save(fraud);
    }
}