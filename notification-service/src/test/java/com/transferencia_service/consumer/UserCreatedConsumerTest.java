package com.transferencia_service.consumer;

import com.transferencia_service.event.UserCreatedEvent;
import com.transferencia_service.service.NotificationService;
import com.transferencia_service.stub.UserCreatedEventStub;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserCreatedConsumerTest {

    private final NotificationService notificationService;
    private final UserCreatedConsumer userCreatedConsumer;

    UserCreatedConsumerTest() {
        this.notificationService = mock(NotificationService.class);
        this.userCreatedConsumer = new UserCreatedConsumer(notificationService);
    }

    @Test
    void consume_DeveProcessarEventoComSucesso_QuandoNotificationServiceNaoLancarExcecao() {
        UserCreatedEvent event = UserCreatedEventStub.buildEvent();

        assertDoesNotThrow(() ->
                userCreatedConsumer.consume(event, 1, 10L, "user-created-topic")
        );

        verify(notificationService).processUserCreatedEvent(event);
    }

    @Test
    void consume_DeveRelancarExcecao_QuandoNotificationServiceLancarErro() {
        UserCreatedEvent event = UserCreatedEventStub.buildEvent();

        doThrow(new RuntimeException("Erro ao processar evento"))
                .when(notificationService)
                .processUserCreatedEvent(event);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userCreatedConsumer.consume(event, 1, 10L, "user-created-topic")
        );

        assertEquals("Erro ao processar evento", exception.getMessage());
        verify(notificationService).processUserCreatedEvent(event);
    }
}