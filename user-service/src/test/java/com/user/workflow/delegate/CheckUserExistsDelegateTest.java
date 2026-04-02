package com.user.workflow.delegate;

import com.user.repository.UserRepository;
import com.user.stub.UserRegistrationDataStub;
import com.user.workflow.dto.UserRegistrationData;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class CheckUserExistsDelegateTest {

    private final UserRepository userRepository;
    private final CheckUserExistsDelegate checkUserExistsDelegate;

    CheckUserExistsDelegateTest() {
        this.userRepository = mock(UserRepository.class);
        this.checkUserExistsDelegate = new CheckUserExistsDelegate(userRepository);
    }

    @Test
    void execute_DeveDefinirUserExistsComoFalse_QuandoUsuarioNaoExistir() {
        DelegateExecution execution = mock(DelegateExecution.class);
        UserRegistrationData registrationData = UserRegistrationDataStub.buildResponse();

        when(execution.getVariable("registrationData")).thenReturn(registrationData);
        when(userRepository.existsByEmail(registrationData.getEmail())).thenReturn(false);
        when(userRepository.existsByCpf(registrationData.getCpf())).thenReturn(false);
        when(userRepository.existsByPhone(registrationData.getPhone())).thenReturn(false);

        checkUserExistsDelegate.execute(execution);

        verify(execution).setVariable("userExists", false);
        verify(execution, never()).setVariable(eq("errorMessage"), any());
    }

    @Test
    void execute_DeveDefinirUserExistsComoTrueEDefinirErrorMessage_QuandoEmailJaExistir() {
        DelegateExecution execution = mock(DelegateExecution.class);
        UserRegistrationData registrationData = UserRegistrationDataStub.buildResponse();

        when(execution.getVariable("registrationData")).thenReturn(registrationData);
        when(userRepository.existsByEmail(registrationData.getEmail())).thenReturn(true);
        when(userRepository.existsByCpf(registrationData.getCpf())).thenReturn(false);
        when(userRepository.existsByPhone(registrationData.getPhone())).thenReturn(false);

        checkUserExistsDelegate.execute(execution);

        verify(execution).setVariable("userExists", true);
        verify(execution).setVariable("errorMessage", "Usuário já cadastrado");
    }

    @Test
    void execute_DeveDefinirUserExistsComoTrueEDefinirErrorMessage_QuandoCpfJaExistir() {
        DelegateExecution execution = mock(DelegateExecution.class);
        UserRegistrationData registrationData = UserRegistrationDataStub.buildResponse();

        when(execution.getVariable("registrationData")).thenReturn(registrationData);
        when(userRepository.existsByEmail(registrationData.getEmail())).thenReturn(false);
        when(userRepository.existsByCpf(registrationData.getCpf())).thenReturn(true);
        when(userRepository.existsByPhone(registrationData.getPhone())).thenReturn(false);

        checkUserExistsDelegate.execute(execution);

        verify(execution).setVariable("userExists", true);
        verify(execution).setVariable("errorMessage", "Usuário já cadastrado");
    }

    @Test
    void execute_DeveDefinirUserExistsComoTrueEDefinirErrorMessage_QuandoTelefoneJaExistir() {
        DelegateExecution execution = mock(DelegateExecution.class);
        UserRegistrationData registrationData = UserRegistrationDataStub.buildResponse();

        when(execution.getVariable("registrationData")).thenReturn(registrationData);
        when(userRepository.existsByEmail(registrationData.getEmail())).thenReturn(false);
        when(userRepository.existsByCpf(registrationData.getCpf())).thenReturn(false);
        when(userRepository.existsByPhone(registrationData.getPhone())).thenReturn(true);

        checkUserExistsDelegate.execute(execution);

        verify(execution).setVariable("userExists", true);
        verify(execution).setVariable("errorMessage", "Usuário já cadastrado");
    }
}