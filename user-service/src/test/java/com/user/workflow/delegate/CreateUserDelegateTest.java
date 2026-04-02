package com.user.workflow.delegate;

import com.user.entities.UserEntity;
import com.user.repository.UserRepository;
import com.user.stub.UserDataStub;
import com.user.stub.UserEntityStub;
import com.user.stub.UserRegistrationDataStub;
import com.user.workflow.dto.UserData;
import com.user.workflow.dto.UserRegistrationData;
import com.user.workflow.mapper.ProcessVariableMapper;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateUserDelegateTest {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProcessVariableMapper processVariableMapper;
    private final CreateUserDelegate createUserDelegate;

    CreateUserDelegateTest() {
        this.userRepository = mock(UserRepository.class);
        this.passwordEncoder = mock(PasswordEncoder.class);
        this.processVariableMapper = mock(ProcessVariableMapper.class);
        this.createUserDelegate = new CreateUserDelegate(userRepository, passwordEncoder, processVariableMapper);
    }

    @Test
    void execute_DeveCriarUsuarioESalvarUserDataNoProcesso_QuandoRegistrationDataForValido() {
        DelegateExecution execution = mock(DelegateExecution.class);
        UserRegistrationData registrationData = UserRegistrationDataStub.buildResponse();
        UserEntity savedUser = UserEntityStub.buildEntity();
        UserData userData = UserDataStub.buildResponse();

        when(execution.getVariable("registrationData")).thenReturn(registrationData);
        when(passwordEncoder.encode(registrationData.getPassword())).thenReturn("senha-criptografada");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
        when(processVariableMapper.toProcessVariable(savedUser)).thenReturn(userData);

        createUserDelegate.execute(execution);

        verify(passwordEncoder).encode(registrationData.getPassword());
        verify(userRepository).save(argThat(user ->
                user.getName().equals(registrationData.getName()) &&
                        user.getEmail().equals(registrationData.getEmail()) &&
                        user.getPassword().equals("senha-criptografada") &&
                        user.getCpf().equals(registrationData.getCpf()) &&
                        user.getPhone().equals(registrationData.getPhone()) &&
                        user.isActive()
        ));
        verify(processVariableMapper).toProcessVariable(savedUser);
        verify(execution).setVariable("userData", userData);
    }
}