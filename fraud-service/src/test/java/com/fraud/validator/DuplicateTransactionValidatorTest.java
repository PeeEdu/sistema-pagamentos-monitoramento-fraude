package com.fraud.validator;

import com.fraud.entity.FraudAnalysisResult;
import com.fraud.event.TransferInitiatedEvent;
import com.fraud.stub.TransferInitiatedEventStub;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DuplicateTransactionValidatorTest {

    @Test
    void validate_DeveRetornarFraude_QuandoTransacaoDuplicadaExistir() {
        RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);
        TransferInitiatedEvent event = TransferInitiatedEventStub.buildEvent();

        when(redisTemplate.hasKey("transfer:duplicate:123456:joao.silva@email.com:1000.00"))
                .thenReturn(true);

        DuplicateTransactionValidator validator = new DuplicateTransactionValidator(redisTemplate);

        FraudAnalysisResult result = validator.validate(event);

        assertNotNull(result);
        assertEquals(event.getTransferId(), result.getTransferId());
        assertTrue(result.isFraud());
        assertEquals(1, result.getFraudTypes().size());
        assertEquals(60.0, result.getRiskScore());
    }

    @Test
    void validate_DeveRetornarSemFraudeERegistrarNoRedis_QuandoTransacaoNaoExistir() {
        RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);

        TransferInitiatedEvent event = TransferInitiatedEventStub.buildEvent();
        String key = "transfer:duplicate:123456:joao.silva@email.com:1000.00";

        when(redisTemplate.hasKey(key)).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        DuplicateTransactionValidator validator = new DuplicateTransactionValidator(redisTemplate);

        FraudAnalysisResult result = validator.validate(event);

        assertNotNull(result);
        assertEquals(event.getTransferId(), result.getTransferId());
        assertFalse(result.isFraud());
        assertTrue(result.getFraudTypes().isEmpty());
        assertEquals(0.0, result.getRiskScore());

        verify(redisTemplate).hasKey(key);
        verify(redisTemplate).opsForValue();
        verify(valueOperations).set(key, "1", 1, TimeUnit.MINUTES);
    }
}