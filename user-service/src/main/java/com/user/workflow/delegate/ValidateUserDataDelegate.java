package com.user.workflow.delegate;

import com.user.workflow.dto.UserRegistrationData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("validateUserDataDelegate")
@RequiredArgsConstructor
public class ValidateUserDataDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        log.info("🔍 Validating user registration data...");

        UserRegistrationData data = (UserRegistrationData)
                execution.getVariable("registrationData");

        // Validações básicas (se necessário)
        if (data == null) {
            throw new IllegalArgumentException("Registration data cannot be null");
        }

        if (data.getEmail() == null || data.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (data.getCpf() == null || data.getCpf().isBlank()) {
            throw new IllegalArgumentException("CPF is required");
        }

        log.info("✅ User data validation passed for email: {}", data.getEmail());
    }
}