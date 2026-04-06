package com.transferencia_service.consumer;

import com.transferencia_service.event.PasswordResetRequestedEvent;
import com.transferencia_service.service.NotificationService;
import com.transferencia_service.stub.PasswordResetRequestedEventStub;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PasswordResetConsumerTest {

    private final NotificationService notificationService;
    private final PasswordResetConsumer passwordResetConsumer;

    PasswordResetConsumerTest() {
        this.notificationService = mock(NotificationService.class);
        this.passwordResetConsumer = new PasswordResetConsumer(notificationService);
    }

    @Test
    void consume_DeveProcessarEventoComSucesso_QuandoNotificationServiceNaoLancarExcecao() {
        PasswordResetRequestedEvent event = PasswordResetRequestedEventStub.buildEvent();

        assertDoesNotThrow(() ->
                passwordResetConsumer.consume(event, 1, 10L, "password-reset-topic")
        );

        verify(notificationService).processPasswordResetEvent(event);
    }

    @Test
    void consume_DeveRelancarExcecao_QuandoNotificationServiceLancarErro() {
        PasswordResetRequestedEvent event = PasswordResetRequestedEventStub.buildEvent();

        doThrow(new RuntimeException("Erro ao processar reset"))
                .when(notificationService)
                .processPasswordResetEvent(event);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> passwordResetConsumer.consume(event, 1, 10L, "password-reset-topic")
        );

        assertEquals("Erro ao processar reset", exception.getMessage());
        verify(notificationService).processPasswordResetEvent(event);
    }
}