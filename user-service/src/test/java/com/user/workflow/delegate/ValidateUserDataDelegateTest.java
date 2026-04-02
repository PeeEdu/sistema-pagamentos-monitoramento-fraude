package com.user.workflow.delegate;

import com.user.stub.UserRegistrationDataStub;
import com.user.workflow.dto.UserRegistrationData;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ValidateUserDataDelegateTest {

    private final ValidateUserDataDelegate validateUserDataDelegate = new ValidateUserDataDelegate();

    @Test
    void execute_DeveExecutarSemLancarExcecao_QuandoRegistrationDataForValido() {
        DelegateExecution execution = mock(DelegateExecution.class);
        UserRegistrationData registrationData = UserRegistrationDataStub.buildResponse();

        when(execution.getVariable("registrationData")).thenReturn(registrationData);

        validateUserDataDelegate.execute(execution);

        verify(execution).getVariable("registrationData");
    }

    @Test
    void execute_DeveLancarIllegalArgumentException_QuandoRegistrationDataForNulo() {
        DelegateExecution execution = mock(DelegateExecution.class);

        when(execution.getVariable("registrationData")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validateUserDataDelegate.execute(execution)
        );

        assertEquals("Registration data cannot be null", exception.getMessage());
    }

    @Test
    void execute_DeveLancarIllegalArgumentException_QuandoEmailForNulo() {
        DelegateExecution execution = mock(DelegateExecution.class);
        UserRegistrationData registrationData = UserRegistrationDataStub.buildResponse();
        registrationData.setEmail(null);

        when(execution.getVariable("registrationData")).thenReturn(registrationData);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validateUserDataDelegate.execute(execution)
        );

        assertEquals("Email is required", exception.getMessage());
    }

    @Test
    void execute_DeveLancarIllegalArgumentException_QuandoEmailForBlank() {
        DelegateExecution execution = mock(DelegateExecution.class);
        UserRegistrationData registrationData = UserRegistrationDataStub.buildResponse();
        registrationData.setEmail("   ");

        when(execution.getVariable("registrationData")).thenReturn(registrationData);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validateUserDataDelegate.execute(execution)
        );

        assertEquals("Email is required", exception.getMessage());
    }

    @Test
    void execute_DeveLancarIllegalArgumentException_QuandoCpfForNulo() {
        DelegateExecution execution = mock(DelegateExecution.class);
        UserRegistrationData registrationData = UserRegistrationDataStub.buildResponse();
        registrationData.setCpf(null);

        when(execution.getVariable("registrationData")).thenReturn(registrationData);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validateUserDataDelegate.execute(execution)
        );

        assertEquals("CPF is required", exception.getMessage());
    }

    @Test
    void execute_DeveLancarIllegalArgumentException_QuandoCpfForBlank() {
        DelegateExecution execution = mock(DelegateExecution.class);
        UserRegistrationData registrationData = UserRegistrationDataStub.buildResponse();
        registrationData.setCpf("   ");

        when(execution.getVariable("registrationData")).thenReturn(registrationData);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validateUserDataDelegate.execute(execution)
        );

        assertEquals("CPF is required", exception.getMessage());
    }
}