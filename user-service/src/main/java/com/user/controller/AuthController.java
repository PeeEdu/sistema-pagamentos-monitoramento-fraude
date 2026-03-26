package com.user.controller;

import com.user.dto.request.LoginRequest;
import com.user.dto.request.RegisterRequest;
import com.user.dto.response.AuthResponse;
import com.user.service.AuthService;
import com.user.workflow.dto.UserRegistrationData;
import com.user.workflow.mapper.ProcessVariableMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RuntimeService runtimeService;
    private final ProcessVariableMapper processMapper;
    private final HistoryService historyService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for CPF: ***.***.***-{}",
                request.getCpf().substring(Math.max(0, request.getCpf().length() - 2)));
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {

        UserRegistrationData registrationData = processMapper.toProcessVariable(request);

        ProcessInstance pi = runtimeService
                .createMessageCorrelation("Register User")
                .setVariable("registrationData", registrationData)
                .correlateStartMessage();

        HistoricVariableInstance authVar = historyService
                .createHistoricVariableInstanceQuery()
                .processInstanceId(pi.getProcessInstanceId())
                .variableName("authResponse")
                .singleResult();

        if (authVar != null) {
            AuthResponse response = (AuthResponse) authVar.getValue();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        HistoricVariableInstance errorVar = historyService
                .createHistoricVariableInstanceQuery()
                .processInstanceId(pi.getProcessInstanceId())
                .variableName("errorMessage")
                .singleResult();

        if (errorVar != null) {
            throw new RuntimeException(String.valueOf(errorVar.getValue()));
        }

        throw new RuntimeException("Processo de registro finalizado sem authResponse");
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        boolean isValid = authService.validateToken(jwtToken);
        return ResponseEntity.ok(isValid);
    }
}