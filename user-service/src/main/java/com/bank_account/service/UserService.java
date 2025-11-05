package com.bank_account.service;

import com.bank_account.dto.request.CreateUserRequest;
import com.bank_account.dto.response.UserCreateResponse;
import com.bank_account.entities.User;
import com.bank_account.mapper.UserMapper;
import com.bank_account.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public record UserService(
        UserRepository userRepository
) {
    public UserCreateResponse createUser(final CreateUserRequest createUserRequest) {
        if (Objects.isNull(createUserRequest)) {
            throw new IllegalArgumentException("CreateUserRequest cannot be null");
        }

        final User user = UserMapper.toEntity(createUserRequest);

        try {
            log.info("Creating user: {}", user);
            userRepository.save(user);
        } catch (DataAccessException e) {
            log.error("Database error while creating user: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao criar usuário no banco de dados");
        }

        return UserMapper.toResponse(user);
    }
}
