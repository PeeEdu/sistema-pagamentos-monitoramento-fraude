package com.user.workflow.delegate;

import com.user.dto.response.AuthResponse;
import com.user.util.JwtUtil;
import com.user.workflow.dto.UserData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("generateTokenDelegate")
@RequiredArgsConstructor
public class GenerateTokenDelegate implements JavaDelegate {

    private final JwtUtil jwtUtil;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("🔑 Generating JWT token...");

        UserData userData = (UserData) execution.getVariable("userData");

        String token = jwtUtil.generateToken(
                userData.getId(),
                userData.getEmail(),
                userData.getName()
        );

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .id(userData.getId())
                .name(userData.getName())
                .email(userData.getEmail())
                .message("Usuário registrado com sucesso")
                .build();

        execution.setVariable("authResponse", response);

        log.info("✅ JWT token generated successfully for user: {}", userData.getEmail());
    }
}