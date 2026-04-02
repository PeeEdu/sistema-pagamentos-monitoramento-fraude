package com.user.workflow.delegate;

import com.user.repository.UserRepository;
import com.user.workflow.dto.UserRegistrationData;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("checkUserExistsDelegate")
@RequiredArgsConstructor
public class CheckUserExistsDelegate implements JavaDelegate {

    private final UserRepository userRepository;

    @Override
    public void execute(DelegateExecution execution) {
        UserRegistrationData data = (UserRegistrationData) execution.getVariable("registrationData");

        boolean emailExists = userRepository.existsByEmail(data.getEmail());
        boolean cpfExists = userRepository.existsByCpf(data.getCpf());
        boolean phoneExists = userRepository.existsByPhone(data.getPhone());

        boolean userExists = emailExists || cpfExists || phoneExists;

        execution.setVariable("userExists", userExists);

        if (userExists) {
            execution.setVariable("errorMessage", "Usuário já cadastrado");
        }
    }
}