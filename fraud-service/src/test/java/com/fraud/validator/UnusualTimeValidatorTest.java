package com.fraud.validator;

import com.fraud.entity.FraudAnalysisResult;
import com.fraud.event.TransferInitiatedEvent;
import com.fraud.stub.TransferInitiatedEventStub;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnusualTimeValidatorTest {

    private final UnusualTimeValidator validator = new UnusualTimeValidator();

    @Test
    void validate_DeveRetornarResultadoValido() {
        TransferInitiatedEvent event = TransferInitiatedEventStub.buildEvent();

        FraudAnalysisResult result = validator.validate(event);

        assertNotNull(result);
        assertEquals(event.getTransferId(), result.getTransferId());
        assertNotNull(result.getFraudTypes());
        assertTrue(result.getRiskScore() == 0.0 || result.getRiskScore() == 15.0);
    }
}