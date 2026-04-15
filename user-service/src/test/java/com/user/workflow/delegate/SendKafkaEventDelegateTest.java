package com.user.workflow.delegate;

import com.user.entity.UserEntity;
import com.user.event.UserCreatedEvent;
import com.user.mapper.UserMapper;
import com.user.producer.UserEventProducer;
import com.user.repository.UserRepository;
import com.user.stub.UserCreatedEventStub;
import com.user.stub.UserDataStub;
import com.user.stub.UserEntityStub;
import com.user.workflow.dto.UserData;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SendKafkaEventDelegateTest {

    private final UserEventProducer producer;
    private final UserMapper mapper;
    private final UserRepository userRepository;
    private final SendKafkaEventDelegate sendKafkaEventDelegate;

    SendKafkaEventDelegateTest() {
        this.producer = mock(UserEventProducer.class);
        this.mapper = mock(UserMapper.class);
        this.userRepository = mock(UserRepository.class);
        this.sendKafkaEventDelegate = new SendKafkaEventDelegate(producer, mapper, userRepository);
    }

    @Test
    void execute_DeveBuscarUsuarioMapearEventoEEnviarParaKafka_QuandoUsuarioExistir() {
        DelegateExecution execution = mock(DelegateExecution.class);
        UserData userData = UserDataStub.buildResponse();
        UserEntity user = UserEntityStub.buildEntity();
        UserCreatedEvent event = UserCreatedEventStub.buildEvent();

        when(execution.getVariable("userData")).thenReturn(userData);
        when(userRepository.findById(userData.getId())).thenReturn(Optional.of(user));
        when(mapper.toUserCreatedEvent(user)).thenReturn(event);

        sendKafkaEventDelegate.execute(execution);

        verify(userRepository).findById(userData.getId());
        verify(mapper).toUserCreatedEvent(user);
        verify(producer).sendUserCreatedEvent(event);
    }

    @Test
    void execute_DeveLancarRuntimeException_QuandoUsuarioNaoExistir() {
        DelegateExecution execution = mock(DelegateExecution.class);
        UserData userData = UserDataStub.buildResponse();

        when(execution.getVariable("userData")).thenReturn(userData);
        when(userRepository.findById(userData.getId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> sendKafkaEventDelegate.execute(execution)
        );

        assertEquals("User not found with ID: " + userData.getId(), exception.getMessage());

        verify(userRepository).findById(userData.getId());
        verify(mapper, never()).toUserCreatedEvent(any());
        verify(producer, never()).sendUserCreatedEvent(any());
    }
}