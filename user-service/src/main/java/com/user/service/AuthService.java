package com.user.service;

import com.user.dto.request.LoginRequest;
import com.user.dto.request.RegisterRequest;
import com.user.dto.response.AuthResponse;
import com.user.entities.UserEntity;
import com.user.exceptions.InvalidCredentialsException;
import com.user.mapper.UserMapper;
import com.user.producer.UserEventProducer;
import com.user.repository.UserRepository;
import com.user.util.JwtUtil;
import com.user.validator.UserValidator;
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
    private final UserEventProducer userEventProducer;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for CPF: {}", maskCpf(request.getCpf()));

        UserEntity user = userRepository.findByCpf(request.getCpf())
                .orElseThrow(() -> new InvalidCredentialsException("CPF ou senha inválidos"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password for CPF: {}", maskCpf(request.getCpf()));
            throw new InvalidCredentialsException("CPF ou senha inválidos");
        }

        if (!user.isActive()) {
            log.warn("Inactive user tried to login: {}", maskCpf(request.getCpf()));
            throw new InvalidCredentialsException("Usuário inativo");
        }

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getName()
        );

        log.info("User with CPF {} logged in successfully", maskCpf(request.getCpf()));

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .message("Login realizado com sucesso")
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());

        userValidator.validateNewUser(
                request.getEmail(),
                request.getCpf(),
                request.getPhone()
        );

        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .cpf(request.getCpf())
                .phone(request.getPhone())
                .active(true)
                .build();

        user = userRepository.save(user);
        var event = userMapper.toUserCreatedEvent(user);

        userEventProducer.sendUserCreatedEvent(event);

        log.info("📤 User created event sent for userId: {}", user.getId());

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getName()
        );

        log.info("User {} registered successfully", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .message("Usuário registrado com sucesso")
                .build();
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 11) return "***";
        return cpf.substring(0, 3) + ".***.***-" + cpf.substring(9);
    }
}