package com.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.dto.request.LoginRequest;
import com.user.dto.request.RegisterRequest;
import com.user.dto.response.AuthResponse;
import com.user.service.AuthService;
import com.user.stub.AuthResponseStub;
import com.user.stub.LoginRequestStub;
import com.user.stub.RegisterRequestStub;
import com.user.stub.UserRegistrationDataStub;
import com.user.workflow.dto.UserRegistrationData;
import com.user.workflow.mapper.ProcessVariableMapper;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstanceQuery;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private static final String BASE_URL = "/api/auth";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final AuthService authService;
    private final RuntimeService runtimeService;
    private final ProcessVariableMapper processVariableMapper;
    private final HistoryService historyService;
    private final MockMvc mockMvc;

    AuthControllerTest() {
        this.authService = mock(AuthService.class);
        this.runtimeService = mock(RuntimeService.class);
        this.processVariableMapper = mock(ProcessVariableMapper.class);
        this.historyService = mock(HistoryService.class);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(
                        authService,
                        runtimeService,
                        processVariableMapper,
                        historyService
                ))
                .build();
    }

    @Test
    void login_DeveRetornarStatus200ComAuthResponse_QuandoCredenciaisForemValidas() throws Exception {
        LoginRequest request = LoginRequestStub.buildRequest();
        AuthResponse response = AuthResponseStub.buildResponse();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(
                        post(BASE_URL + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(MAPPER.writeValueAsString(response)));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void register_DeveRetornarStatus201ComAuthResponse_QuandoProcessoRetornarAuthResponse() throws Exception {
        RegisterRequest request = RegisterRequestStub.buildRequest();
        UserRegistrationData registrationData = UserRegistrationDataStub.buildResponse();
        AuthResponse authResponse = AuthResponseStub.buildResponse();

        MessageCorrelationBuilder correlationBuilder = mock(MessageCorrelationBuilder.class);
        ProcessInstance processInstance = mock(ProcessInstance.class);
        HistoricVariableInstanceQuery authQuery = mock(HistoricVariableInstanceQuery.class);
        HistoricVariableInstance authVar = mock(HistoricVariableInstance.class);

        when(processVariableMapper.toProcessVariable(any(RegisterRequest.class))).thenReturn(registrationData);

        when(runtimeService.createMessageCorrelation("Register User")).thenReturn(correlationBuilder);
        when(correlationBuilder.setVariable("registrationData", registrationData)).thenReturn(correlationBuilder);
        when(correlationBuilder.correlateStartMessage()).thenReturn(processInstance);
        when(processInstance.getProcessInstanceId()).thenReturn("process-instance-123");

        when(historyService.createHistoricVariableInstanceQuery()).thenReturn(authQuery);
        when(authQuery.processInstanceId("process-instance-123")).thenReturn(authQuery);
        when(authQuery.variableName("authResponse")).thenReturn(authQuery);
        when(authQuery.singleResult()).thenReturn(authVar);
        when(authVar.getValue()).thenReturn(authResponse);

        mockMvc.perform(
                        post(BASE_URL + "/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(MAPPER.writeValueAsString(authResponse)));

        verify(processVariableMapper).toProcessVariable(any(RegisterRequest.class));
        verify(runtimeService).createMessageCorrelation("Register User");
        verify(correlationBuilder).setVariable("registrationData", registrationData);
        verify(correlationBuilder).correlateStartMessage();
        verify(historyService).createHistoricVariableInstanceQuery();
        verify(authQuery).processInstanceId("process-instance-123");
        verify(authQuery).variableName("authResponse");
    }

    @Test
    void register_DeveLancarRuntimeException_QuandoProcessoRetornarErrorMessage() {
        RegisterRequest request = RegisterRequestStub.buildRequest();
        UserRegistrationData registrationData = UserRegistrationDataStub.buildResponse();

        MessageCorrelationBuilder correlationBuilder = mock(MessageCorrelationBuilder.class);
        ProcessInstance processInstance = mock(ProcessInstance.class);

        HistoricVariableInstanceQuery authQuery = mock(HistoricVariableInstanceQuery.class);
        HistoricVariableInstanceQuery errorQuery = mock(HistoricVariableInstanceQuery.class);
        HistoricVariableInstance errorVar = mock(HistoricVariableInstance.class);

        when(processVariableMapper.toProcessVariable(any(RegisterRequest.class))).thenReturn(registrationData);

        when(runtimeService.createMessageCorrelation("Register User")).thenReturn(correlationBuilder);
        when(correlationBuilder.setVariable("registrationData", registrationData)).thenReturn(correlationBuilder);
        when(correlationBuilder.correlateStartMessage()).thenReturn(processInstance);
        when(processInstance.getProcessInstanceId()).thenReturn("process-instance-123");

        when(historyService.createHistoricVariableInstanceQuery())
                .thenReturn(authQuery)
                .thenReturn(errorQuery);

        when(authQuery.processInstanceId("process-instance-123")).thenReturn(authQuery);
        when(authQuery.variableName("authResponse")).thenReturn(authQuery);
        when(authQuery.singleResult()).thenReturn(null);

        when(errorQuery.processInstanceId("process-instance-123")).thenReturn(errorQuery);
        when(errorQuery.variableName("errorMessage")).thenReturn(errorQuery);
        when(errorQuery.singleResult()).thenReturn(errorVar);
        when(errorVar.getValue()).thenReturn("Erro ao registrar usuário");

        Exception exception = assertThrows(Exception.class, () ->
                mockMvc.perform(
                        post(BASE_URL + "/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsBytes(request)))
        );

        assertEquals("Erro ao registrar usuário", exception.getCause().getMessage());

        verify(processVariableMapper).toProcessVariable(any(RegisterRequest.class));
        verify(runtimeService).createMessageCorrelation("Register User");
        verify(correlationBuilder).setVariable("registrationData", registrationData);
        verify(correlationBuilder).correlateStartMessage();
        verify(historyService, times(2)).createHistoricVariableInstanceQuery();
    }

    @Test
    void register_DeveLancarRuntimeException_QuandoProcessoFinalizarSemAuthResponseESemErrorMessage() {
        RegisterRequest request = RegisterRequestStub.buildRequest();
        UserRegistrationData registrationData = UserRegistrationDataStub.buildResponse();

        MessageCorrelationBuilder correlationBuilder = mock(MessageCorrelationBuilder.class);
        ProcessInstance processInstance = mock(ProcessInstance.class);

        HistoricVariableInstanceQuery authQuery = mock(HistoricVariableInstanceQuery.class);
        HistoricVariableInstanceQuery errorQuery = mock(HistoricVariableInstanceQuery.class);

        when(processVariableMapper.toProcessVariable(any(RegisterRequest.class))).thenReturn(registrationData);

        when(runtimeService.createMessageCorrelation("Register User")).thenReturn(correlationBuilder);
        when(correlationBuilder.setVariable("registrationData", registrationData)).thenReturn(correlationBuilder);
        when(correlationBuilder.correlateStartMessage()).thenReturn(processInstance);
        when(processInstance.getProcessInstanceId()).thenReturn("process-instance-123");

        when(historyService.createHistoricVariableInstanceQuery())
                .thenReturn(authQuery)
                .thenReturn(errorQuery);

        when(authQuery.processInstanceId("process-instance-123")).thenReturn(authQuery);
        when(authQuery.variableName("authResponse")).thenReturn(authQuery);
        when(authQuery.singleResult()).thenReturn(null);

        when(errorQuery.processInstanceId("process-instance-123")).thenReturn(errorQuery);
        when(errorQuery.variableName("errorMessage")).thenReturn(errorQuery);
        when(errorQuery.singleResult()).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () ->
                mockMvc.perform(
                        post(BASE_URL + "/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsBytes(request)))
        );

        assertEquals("Processo de registro finalizado sem authResponse", exception.getCause().getMessage());

        verify(processVariableMapper).toProcessVariable(any(RegisterRequest.class));
        verify(runtimeService).createMessageCorrelation("Register User");
        verify(correlationBuilder).setVariable("registrationData", registrationData);
        verify(correlationBuilder).correlateStartMessage();
        verify(historyService, times(2)).createHistoricVariableInstanceQuery();
    }

    @Test
    void validateToken_DeveRetornarStatus200ComTrue_QuandoTokenForValido() throws Exception {
        when(authService.validateToken("mocked-jwt-token")).thenReturn(true);

        mockMvc.perform(
                        post(BASE_URL + "/validate")
                                .header("Authorization", "Bearer mocked-jwt-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(authService).validateToken("mocked-jwt-token");
    }

    @Test
    void validateToken_DeveRetornarStatus200ComFalse_QuandoTokenForInvalido() throws Exception {
        when(authService.validateToken("mocked-jwt-token")).thenReturn(false);

        mockMvc.perform(
                        post(BASE_URL + "/validate")
                                .header("Authorization", "Bearer mocked-jwt-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(authService).validateToken("mocked-jwt-token");
    }
}