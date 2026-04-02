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
import com.fraud.stub.FraudAnalysisResultStub;
import com.fraud.stub.FraudEntityStub;
import com.fraud.stub.TransferInitiatedEventStub;
import com.fraud.stub.TransferValidatedEventStub;
import com.fraud.validator.FraudValidator;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FraudDetectionServiceTest {

    private final FraudRepository fraudRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ListOperations<String, Object> listOperations;
    private final TransferValidatedProducer transferValidatedProducer;
    private final FraudMapper fraudMapper;

    FraudDetectionServiceTest() {
        this.fraudRepository = mock(FraudRepository.class);
        this.redisTemplate = mock(RedisTemplate.class);
        this.listOperations = mock(ListOperations.class);
        this.transferValidatedProducer = mock(TransferValidatedProducer.class);
        this.fraudMapper = mock(FraudMapper.class);

        when(redisTemplate.opsForList()).thenReturn(listOperations);
    }

    @Test
    void analyze_DeveAprovarTransferenciaEEnviarEvento_QuandoRiscoForBaixo() {
        FraudValidator validator1 = mock(FraudValidator.class);
        FraudValidator validator2 = mock(FraudValidator.class);

        TransferInitiatedEvent event = TransferInitiatedEventStub.buildEvent();
        TransferValidatedEvent validatedEvent = TransferValidatedEventStub.buildApprovedEvent();

        when(validator1.validate(event)).thenReturn(
                FraudAnalysisResult.builder()
                        .transferId(event.getTransferId())
                        .isFraud(false)
                        .fraudTypes(List.of())
                        .riskScore(0.0)
                        .build()
        );
        when(validator2.validate(event)).thenReturn(
                FraudAnalysisResult.builder()
                        .transferId(event.getTransferId())
                        .isFraud(true)
                        .fraudTypes(List.of(FraudType.UNUSUAL_AMOUNT))
                        .riskScore(10.0)
                        .build()
        );

        when(fraudMapper.toValidatedEvent(event, true, 10.0, List.of(FraudType.UNUSUAL_AMOUNT)))
                .thenReturn(validatedEvent);

        FraudDetectionService service = new FraudDetectionService(
                List.of(validator1, validator2),
                fraudRepository,
                redisTemplate,
                transferValidatedProducer,
                fraudMapper
        );

        service.analyze(event);

        verify(validator1).validate(event);
        verify(validator2).validate(event);
        verify(redisTemplate).opsForList();
        verify(listOperations).leftPush("transfer:history:123456", event);
        verify(listOperations).trim("transfer:history:123456", 0, 9);
        verify(redisTemplate).expire("transfer:history:123456", 1, TimeUnit.HOURS);
        verify(fraudRepository, never()).save(any(FraudEntity.class));
        verify(fraudMapper).toValidatedEvent(event, true, 10.0, List.of(FraudType.UNUSUAL_AMOUNT));
        verify(transferValidatedProducer).send(validatedEvent);
    }

    @Test
    void analyze_DeveAprovarTransferencia_QuandoRiscoForMedioEAbaixoDe50() {
        FraudValidator validator = mock(FraudValidator.class);

        TransferInitiatedEvent event = TransferInitiatedEventStub.buildEvent();
        TransferValidatedEvent validatedEvent = TransferValidatedEventStub.buildApprovedEvent();

        when(validator.validate(event)).thenReturn(
                FraudAnalysisResult.builder()
                        .transferId(event.getTransferId())
                        .isFraud(true)
                        .fraudTypes(List.of(FraudType.UNUSUAL_PATTERN))
                        .riskScore(40.0)
                        .build()
        );

        when(fraudMapper.toValidatedEvent(event, true, 40.0, List.of(FraudType.UNUSUAL_PATTERN)))
                .thenReturn(validatedEvent);

        FraudDetectionService service = new FraudDetectionService(
                List.of(validator),
                fraudRepository,
                redisTemplate,
                transferValidatedProducer,
                fraudMapper
        );

        service.analyze(event);

        verify(fraudRepository, never()).save(any(FraudEntity.class));
        verify(fraudMapper).toValidatedEvent(event, true, 40.0, List.of(FraudType.UNUSUAL_PATTERN));
        verify(transferValidatedProducer).send(validatedEvent);
    }

    @Test
    void analyze_DeveBloquearSalvarFraudeEEnviarEvento_QuandoRiscoForAlto() {
        FraudValidator validator = mock(FraudValidator.class);

        TransferInitiatedEvent event = TransferInitiatedEventStub.buildEvent();
        TransferValidatedEvent validatedEvent = TransferValidatedEventStub.buildRejectedEvent();
        FraudEntity fraudEntity = FraudEntityStub.buildEntity();

        when(validator.validate(event)).thenReturn(
                FraudAnalysisResult.builder()
                        .transferId(event.getTransferId())
                        .isFraud(true)
                        .fraudTypes(List.of(FraudType.HIGH_VALUE))
                        .riskScore(70.0)
                        .build()
        );

        when(fraudMapper.toFraudEntity(event, 70.0, List.of(FraudType.HIGH_VALUE)))
                .thenReturn(fraudEntity);
        when(fraudMapper.toValidatedEvent(event, false, 70.0, List.of(FraudType.HIGH_VALUE)))
                .thenReturn(validatedEvent);

        FraudDetectionService service = new FraudDetectionService(
                List.of(validator),
                fraudRepository,
                redisTemplate,
                transferValidatedProducer,
                fraudMapper
        );

        service.analyze(event);

        assertEquals(RiskLevel.HIGH, fraudEntity.getRiskLevel());
        assertEquals(FraudStatus.BLOCKED, fraudEntity.getStatus());

        verify(fraudMapper).toFraudEntity(event, 70.0, List.of(FraudType.HIGH_VALUE));
        verify(fraudRepository).save(fraudEntity);
        verify(fraudMapper).toValidatedEvent(event, false, 70.0, List.of(FraudType.HIGH_VALUE));
        verify(transferValidatedProducer).send(validatedEvent);
    }

    @Test
    void analyze_DeveBloquearSalvarFraudeEEnviarEvento_QuandoRiscoForCritico() {
        FraudValidator validator = mock(FraudValidator.class);

        TransferInitiatedEvent event = TransferInitiatedEventStub.buildEvent();
        TransferValidatedEvent validatedEvent = TransferValidatedEventStub.buildRejectedEvent();
        FraudEntity fraudEntity = FraudEntityStub.buildEntity();

        when(validator.validate(event)).thenReturn(
                FraudAnalysisResult.builder()
                        .transferId(event.getTransferId())
                        .isFraud(true)
                        .fraudTypes(List.of(FraudType.MULTIPLE_INDICATORS))
                        .riskScore(95.0)
                        .build()
        );

        when(fraudMapper.toFraudEntity(event, 95.0, List.of(FraudType.MULTIPLE_INDICATORS)))
                .thenReturn(fraudEntity);
        when(fraudMapper.toValidatedEvent(event, false, 95.0, List.of(FraudType.MULTIPLE_INDICATORS)))
                .thenReturn(validatedEvent);

        FraudDetectionService service = new FraudDetectionService(
                List.of(validator),
                fraudRepository,
                redisTemplate,
                transferValidatedProducer,
                fraudMapper
        );

        service.analyze(event);

        assertEquals(RiskLevel.CRITICAL, fraudEntity.getRiskLevel());
        assertEquals(FraudStatus.BLOCKED, fraudEntity.getStatus());

        verify(fraudRepository).save(fraudEntity);
        verify(transferValidatedProducer).send(validatedEvent);
    }

    @Test
    void analyze_DeveSomarRiskScoreEAgruparFraudTypes_QuandoMultiplosValidatorsDetectaremFraude() {
        FraudValidator validator1 = mock(FraudValidator.class);
        FraudValidator validator2 = mock(FraudValidator.class);

        TransferInitiatedEvent event = TransferInitiatedEventStub.buildEvent();
        TransferValidatedEvent validatedEvent = TransferValidatedEventStub.buildRejectedEvent();
        FraudEntity fraudEntity = FraudEntityStub.buildEntity();

        when(validator1.validate(event)).thenReturn(
                FraudAnalysisResult.builder()
                        .transferId(event.getTransferId())
                        .isFraud(true)
                        .fraudTypes(List.of(FraudType.HIGH_VALUE))
                        .riskScore(30.0)
                        .build()
        );

        when(validator2.validate(event)).thenReturn(
                FraudAnalysisResult.builder()
                        .transferId(event.getTransferId())
                        .isFraud(true)
                        .fraudTypes(List.of(FraudType.LOCATION_MISMATCH))
                        .riskScore(40.0)
                        .build()
        );

        List<FraudType> fraudTypes = List.of(FraudType.HIGH_VALUE, FraudType.LOCATION_MISMATCH);

        when(fraudMapper.toFraudEntity(event, 70.0, fraudTypes)).thenReturn(fraudEntity);
        when(fraudMapper.toValidatedEvent(event, false, 70.0, fraudTypes)).thenReturn(validatedEvent);

        FraudDetectionService service = new FraudDetectionService(
                List.of(validator1, validator2),
                fraudRepository,
                redisTemplate,
                transferValidatedProducer,
                fraudMapper
        );

        service.analyze(event);

        verify(fraudMapper).toFraudEntity(event, 70.0, fraudTypes);
        verify(fraudMapper).toValidatedEvent(event, false, 70.0, fraudTypes);
        verify(fraudRepository).save(fraudEntity);
        verify(transferValidatedProducer).send(validatedEvent);
    }
    @Test
    void analyze_DeveBloquearTransferencia_QuandoRiscoForMedioEMaiorOuIgualA50() {
        FraudValidator validator = mock(FraudValidator.class);

        TransferInitiatedEvent event = TransferInitiatedEventStub.buildEvent();
        TransferValidatedEvent validatedEvent = TransferValidatedEventStub.buildRejectedEvent();
        FraudEntity fraudEntity = FraudEntityStub.buildEntity();

        when(validator.validate(event)).thenReturn(
                FraudAnalysisResult.builder()
                        .transferId(event.getTransferId())
                        .isFraud(true)
                        .fraudTypes(List.of(FraudType.UNUSUAL_PATTERN))
                        .riskScore(55.0)
                        .build()
        );

        when(fraudMapper.toFraudEntity(event, 55.0, List.of(FraudType.UNUSUAL_PATTERN)))
                .thenReturn(fraudEntity);
        when(fraudMapper.toValidatedEvent(event, false, 55.0, List.of(FraudType.UNUSUAL_PATTERN)))
                .thenReturn(validatedEvent);

        FraudDetectionService service = new FraudDetectionService(
                List.of(validator),
                fraudRepository,
                redisTemplate,
                transferValidatedProducer,
                fraudMapper
        );

        service.analyze(event);

        verify(fraudRepository).save(fraudEntity);
        verify(transferValidatedProducer).send(validatedEvent);
    }
}