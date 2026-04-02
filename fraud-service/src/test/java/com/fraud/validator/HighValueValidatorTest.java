package com.fraud.validator;

import com.fraud.entity.FraudAnalysisResult;
import com.fraud.event.TransferInitiatedEvent;
import com.fraud.stub.TransferInitiatedEventStub;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class HighValueValidatorTest {

    private final HighValueValidator validator = new HighValueValidator();

    @Test
    void validate_DeveRetornarFraude_QuandoValorForMaiorQueLimite() {
        TransferInitiatedEvent event = TransferInitiatedEventStub.buildHighAmountEvent();

        FraudAnalysisResult result = validator.validate(event);

        assertNotNull(result);
        assertEquals(event.getTransferId(), result.getTransferId());
        assertTrue(result.isFraud());
        assertEquals(1, result.getFraudTypes().size());
        assertEquals(40.0, result.getRiskScore());
    }

    @Test
    void validate_DeveRetornarSemFraude_QuandoValorForMenorOuIgualAoLimite() {
        TransferInitiatedEvent event = TransferInitiatedEventStub.buildEvent();

        FraudAnalysisResult result = validator.validate(event);

        assertNotNull(result);
        assertEquals(event.getTransferId(), result.getTransferId());
        assertFalse(result.isFraud());
        assertTrue(result.getFraudTypes().isEmpty());
        assertEquals(0.0, result.getRiskScore());
    }
}