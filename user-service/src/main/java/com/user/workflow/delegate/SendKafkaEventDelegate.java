package com.user.workflow.delegate;

import com.user.entities.UserEntity;
import com.user.mapper.UserMapper;
import com.user.producer.UserEventProducer;
import com.user.repository.UserRepository;
import com.user.workflow.dto.UserData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("sendKafkaEventDelegate")
@RequiredArgsConstructor
public class SendKafkaEventDelegate implements JavaDelegate {

    private final UserEventProducer producer;
    private final UserMapper mapper;
    private final UserRepository userRepository;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("📤 Sending user created event to Kafka...");

        UserData userData = (UserData) execution.getVariable("userData");

        UserEntity user = userRepository.findById(userData.getId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userData.getId()));

        producer.sendUserCreatedEvent(
                mapper.toUserCreatedEvent(user)
        );



        log.info("✅ Kafka event sent successfully for user ID: {}", userData.getId());
    }
}