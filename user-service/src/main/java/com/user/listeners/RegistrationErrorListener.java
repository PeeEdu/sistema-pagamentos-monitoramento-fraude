package com.user.listeners;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RegistrationErrorListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        String errorMessage = (String) execution.getVariable("errorMessage");
        log.error("❌ Erro no processo de registro: {}", errorMessage);
    }
}
