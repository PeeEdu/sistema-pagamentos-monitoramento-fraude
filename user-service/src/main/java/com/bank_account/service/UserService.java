package com.bank_account.service;

import com.bank_account.dto.request.CreateUserRequest;
import com.bank_account.dto.response.UserCreateResponse;
import com.bank_account.dto.response.UserResponse;
import com.bank_account.entities.UserEntity;
import com.bank_account.exceptions.UserAlreadyExistsException;
import com.bank_account.exceptions.UserNotFoundException;
import com.bank_account.mapper.UserMapper;
import com.bank_account.producer.BankAccountEventProducer;
import com.bank_account.producer.UserEventProducer;
import com.bank_account.repository.UserRepository;
import com.bank_account.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final UserEventProducer userEventProducer;
    private final BankAccountEventProducer bankAccountEventProducer;

    public UserCreateResponse createUser(final CreateUserRequest createUserRequest) {
        final UserEntity userEntity = userMapper.toEntity(createUserRequest);

        userValidator.validateCpfNotExists(createUserRequest.cpf());
        userValidator.validateEmailNotExists(createUserRequest.email());

        log.info("Creating user: {}", userEntity);
        
        var savedUser = userRepository.save(userEntity);

        var event = userMapper.toUserCreatedEvent(savedUser);
        userEventProducer.sendUserCreatedEvent(event);

        var idUser = userMapper.toBankAccountCreateEvent(savedUser);
        bankAccountEventProducer.sendToCreateBankAccount(idUser);

        return userMapper.toCreateResponse(savedUser);
    }

    public List<UserResponse> findAll(){
        final List<UserEntity> userEntity = userRepository.findAll();

        return userEntity.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse findUserById(final String id) {
        Optional<UserEntity> userEntity = userRepository.findById(id);

        return userEntity
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }


}
