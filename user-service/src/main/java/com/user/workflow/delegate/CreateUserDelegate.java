package com.user.workflow.delegate;

import com.user.entity.UserEntity;
import com.user.repository.UserRepository;
import com.user.workflow.dto.UserData;
import com.user.workflow.dto.UserRegistrationData;
import com.user.workflow.mapper.ProcessVariableMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component("createUserDelegate")
@RequiredArgsConstructor
public class CreateUserDelegate implements JavaDelegate {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProcessVariableMapper processMapper;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("👤 Creating new user...");

        UserRegistrationData regData = (UserRegistrationData)
                execution.getVariable("registrationData");

        UserEntity user = UserEntity.builder()
                .name(regData.getName())
                .email(regData.getEmail())
                .password(passwordEncoder.encode(regData.getPassword()))
                .cpf(regData.getCpf())
                .phone(regData.getPhone())
                .active(true)
                .build();

        user = userRepository.save(user);

        UserData userData = processMapper.toProcessVariable(user);
        execution.setVariable("userData", userData);

        log.info("✅ User created successfully with ID: {}", user.getId());
    }
}