package com.user.producer;

import com.user.event.PasswordResetRequestedEvent;
import com.user.event.UserCreatedEvent;
import com.user.stub.UserCreatedEventStub;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class UserEventProducerTest {

    private KafkaTemplate<String, Object> kafkaTemplate;
    private UserEventProducer userEventProducer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        userEventProducer = new UserEventProducer(kafkaTemplate);

        ReflectionTestUtils.setField(userEventProducer, "userCreatedTopic", "user-created-topic");
        ReflectionTestUtils.setField(userEventProducer, "passwordResetTopic", "password-reset-topic");
    }

    @Test
    void sendUserCreatedEvent_DeveEnviarEventoParaTopicoCorreto_QuandoEventoForValido() {
        UserCreatedEvent event = UserCreatedEventStub.buildEvent();

        @SuppressWarnings("unchecked")
        SendResult<String, Object> sendResult = mock(SendResult.class);
        RecordMetadata recordMetadata = mock(RecordMetadata.class);

        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        when(recordMetadata.offset()).thenReturn(10L);

        CompletableFuture<SendResult<String, Object>> future =
                CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send("user-created-topic", event.getUserId(), event))
                .thenReturn(future);

        assertDoesNotThrow(() -> userEventProducer.sendUserCreatedEvent(event));

        verify(kafkaTemplate).send("user-created-topic", event.getUserId(), event);
    }

    @Test
    void sendUserCreatedEvent_DeveExecutarSemLancarExcecao_QuandoEnvioForBemSucedido() {
        UserCreatedEvent event = UserCreatedEventStub.buildEvent();

        @SuppressWarnings("unchecked")
        SendResult<String, Object> sendResult = mock(SendResult.class);
        RecordMetadata recordMetadata = mock(RecordMetadata.class);

        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        when(recordMetadata.offset()).thenReturn(10L);

        CompletableFuture<SendResult<String, Object>> future =
                CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send("user-created-topic", event.getUserId(), event))
                .thenReturn(future);

        assertDoesNotThrow(() -> userEventProducer.sendUserCreatedEvent(event));

        verify(kafkaTemplate).send("user-created-topic", event.getUserId(), event);
    }

    @Test
    void sendUserCreatedEvent_DeveExecutarSemLancarExcecao_QuandoEnvioFalhar() {
        UserCreatedEvent event = UserCreatedEventStub.buildEvent();

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Erro ao enviar evento"));

        when(kafkaTemplate.send("user-created-topic", event.getUserId(), event))
                .thenReturn(future);

        assertDoesNotThrow(() -> userEventProducer.sendUserCreatedEvent(event));

        verify(kafkaTemplate).send("user-created-topic", event.getUserId(), event);
    }

    @Test
    void sendPasswordResetEvent_DeveEnviarEventoParaTopicoCorreto_QuandoEventoForValido() {
        PasswordResetRequestedEvent event = createPasswordResetEvent();

        @SuppressWarnings("unchecked")
        SendResult<String, Object> sendResult = mock(SendResult.class);
        RecordMetadata recordMetadata = mock(RecordMetadata.class);

        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        when(recordMetadata.offset()).thenReturn(10L);

        CompletableFuture<SendResult<String, Object>> future =
                CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send("password-reset-topic", event.getUserId(), event))
                .thenReturn(future);

        assertDoesNotThrow(() -> userEventProducer.sendPasswordResetEvent(event));

        verify(kafkaTemplate).send("password-reset-topic", event.getUserId(), event);
    }

    @Test
    void sendPasswordResetEvent_DeveExecutarSemLancarExcecao_QuandoEnvioForBemSucedido() {
        PasswordResetRequestedEvent event = createPasswordResetEvent();

        @SuppressWarnings("unchecked")
        SendResult<String, Object> sendResult = mock(SendResult.class);
        RecordMetadata recordMetadata = mock(RecordMetadata.class);

        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        when(recordMetadata.offset()).thenReturn(10L);

        CompletableFuture<SendResult<String, Object>> future =
                CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send("password-reset-topic", event.getUserId(), event))
                .thenReturn(future);

        assertDoesNotThrow(() -> userEventProducer.sendPasswordResetEvent(event));

        verify(kafkaTemplate).send("password-reset-topic", event.getUserId(), event);
    }

    @Test
    void sendPasswordResetEvent_DeveExecutarSemLancarExcecao_QuandoEnvioFalhar() {
        PasswordResetRequestedEvent event = createPasswordResetEvent();

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Erro ao enviar evento"));

        when(kafkaTemplate.send("password-reset-topic", event.getUserId(), event))
                .thenReturn(future);

        assertDoesNotThrow(() -> userEventProducer.sendPasswordResetEvent(event));

        verify(kafkaTemplate).send("password-reset-topic", event.getUserId(), event);
    }

    private PasswordResetRequestedEvent createPasswordResetEvent() {
        return new PasswordResetRequestedEvent(
                "user-123",
                "user@email.com",
                "reset-token-123",
                LocalDateTime.now(),
                "João Silva"
        );
    }
}