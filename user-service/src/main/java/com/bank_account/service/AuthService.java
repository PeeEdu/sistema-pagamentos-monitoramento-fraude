package com.bank_account.service;

import com.bank_account.dto.request.LoginRequest;
import com.bank_account.dto.request.RegisterRequest;
import com.bank_account.dto.response.AuthResponse;
import com.bank_account.entities.UserEntity;
import com.bank_account.exceptions.InvalidCredentialsException;
import com.bank_account.exceptions.UserAlreadyExistsException;
import com.bank_account.repository.UserRepository;
import com.bank_account.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Email ou senha inválidos"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password for email: {}", request.getEmail());
            throw new InvalidCredentialsException("Email ou senha inválidos");
        }

        if (!user.isActive()) {
            log.warn("Inactive user tried to login: {}", request.getEmail());
            throw new InvalidCredentialsException("Usuário inativo");
        }

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getName()
        );

        log.info("User {} logged in successfully", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .message("Login realizado com sucesso")
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email já está em uso");
        }

        if (userRepository.existsByCpf(request.getCpf())) {
            throw new UserAlreadyExistsException("CPF já está em uso");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new UserAlreadyExistsException("Telefone já está em uso");
        }

        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .cpf(request.getCpf())
                .phone(request.getPhone())
                .active(true)
                .build();

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getName()
        );

        log.info("User {} registered successfully", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .message("Usuário registrado com sucesso")
                .build();
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
}