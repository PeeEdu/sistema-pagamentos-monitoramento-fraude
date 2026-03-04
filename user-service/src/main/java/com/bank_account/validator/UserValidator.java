package com.bank_account.validator;

import com.bank_account.exceptions.UserAlreadyExistsException;
import com.bank_account.exceptions.UserNotFoundException;
import com.bank_account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateEmailNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            log.warn("Attempt to create user with existing email: {}", email);
            throw new UserAlreadyExistsException("Usuário com email '" + email + "' já existe");
        }
    }

    public void validateCpfNotExists(String cpf) {
        if (userRepository.existsByCpf(cpf)) {
            log.warn("Attempt to create user with existing CPF: {}", cpf);
            throw new UserAlreadyExistsException("Usuário com CPF '" + cpf + "' já existe");
        }
    }

    public void validateUserExists(String id) {
        if (!userRepository.existsById(id)) {
            log.warn("User not found with id: {}", id);
            throw new UserNotFoundException("Usuário com ID '" + id + "' não encontrado");
        }
    }
}