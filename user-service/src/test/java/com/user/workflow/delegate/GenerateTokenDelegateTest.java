package com.user.workflow.delegate;

import com.user.dto.response.AuthResponse;
import com.user.stub.UserDataStub;
import com.user.util.JwtUtil;
import com.user.workflow.dto.UserData;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GenerateTokenDelegateTest {

    private final JwtUtil jwtUtil;
    private final GenerateTokenDelegate generateTokenDelegate;

    GenerateTokenDelegateTest() {
        this.jwtUtil = mock(JwtUtil.class);
        this.generateTokenDelegate = new GenerateTokenDelegate(jwtUtil);
    }

    @Test
    void execute_DeveGerarTokenESalvarAuthResponseNoProcesso_QuandoUserDataForValido() {
        DelegateExecution execution = mock(DelegateExecution.class);
        UserData userData = UserDataStub.buildResponse();

        when(execution.getVariable("userData")).thenReturn(userData);
        when(jwtUtil.generateToken(userData.getId(), userData.getEmail(), userData.getName()))
                .thenReturn("mocked-jwt-token");

        generateTokenDelegate.execute(execution);

        verify(jwtUtil).generateToken(userData.getId(), userData.getEmail(), userData.getName());
        verify(execution).setVariable(eq("authResponse"), argThat(value -> {
            AuthResponse response = (AuthResponse) value;
            return response.getToken().equals("mocked-jwt-token")
                    && response.getId().equals(userData.getId())
                    && response.getName().equals(userData.getName())
                    && response.getEmail().equals(userData.getEmail())
                    && response.getMessage().equals("Usuário registrado com sucesso");
        }));
    }
}