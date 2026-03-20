package com.user.validator;

import com.user.exceptions.UserAlreadyExistsException;
import com.user.exceptions.UserNotFoundException;
import com.user.repository.UserRepository;
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

    public void validatePhoneNotExists(String phone) {  // ← ADICIONAR
        if (userRepository.existsByPhone(phone)) {
            log.warn("Attempt to create user with existing phone: {}", phone);
            throw new UserAlreadyExistsException("Usuário com telefone '" + phone + "' já existe");
        }
    }

    public void validateUserExists(String id) {
        if (!userRepository.existsById(id)) {
            log.warn("User not found with id: {}", id);
            throw new UserNotFoundException("Usuário com ID '" + id + "' não encontrado");
        }
    }

    // ✅ MÉTODO HELPER: Validar tudo de uma vez
    public void validateNewUser(String email, String cpf, String phone) {
        validateEmailNotExists(email);
        validateCpfNotExists(cpf);
        validatePhoneNotExists(phone);
    }
}