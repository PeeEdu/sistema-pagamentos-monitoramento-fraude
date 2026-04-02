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

class HighFrequencyValidatorTest {

    @Test
    void validate_DeveRetornarSemFraudeEConfigurarExpiracao_QuandoForPrimeiraTransacao() {
        RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);

        TransferInitiatedEvent event = TransferInitiatedEventStub.buildEvent();
        String key = "transfer:count:123456";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(key)).thenReturn(1L);

        HighFrequencyValidator validator = new HighFrequencyValidator(redisTemplate);

        FraudAnalysisResult result = validator.validate(event);

        assertNotNull(result);
        assertEquals(event.getTransferId(), result.getTransferId());
        assertFalse(result.isFraud());
        assertTrue(result.getFraudTypes().isEmpty());
        assertEquals(0.0, result.getRiskScore());

        verify(redisTemplate).opsForValue();
        verify(valueOperations).increment(key);
        verify(redisTemplate).expire(key, 1, TimeUnit.MINUTES);
    }

    @Test
    void validate_DeveRetornarFraude_QuandoQuantidadeUltrapassarLimite() {
        RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);

        TransferInitiatedEvent event = TransferInitiatedEventStub.buildEvent();
        String key = "transfer:count:123456";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(key)).thenReturn(6L);

        HighFrequencyValidator validator = new HighFrequencyValidator(redisTemplate);

        FraudAnalysisResult result = validator.validate(event);

        assertNotNull(result);
        assertEquals(event.getTransferId(), result.getTransferId());
        assertTrue(result.isFraud());
        assertEquals(1, result.getFraudTypes().size());
        assertEquals(30.0, result.getRiskScore());

        verify(redisTemplate).opsForValue();
        verify(valueOperations).increment(key);
        verify(redisTemplate, never()).expire(key, 1, TimeUnit.MINUTES);
    }
}