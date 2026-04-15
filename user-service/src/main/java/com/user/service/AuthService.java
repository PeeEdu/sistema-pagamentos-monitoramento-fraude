package com.user.service;

import com.user.dto.request.LoginRequest;
import com.user.dto.request.RegisterRequest;
import com.user.dto.response.AuthResponse;
import com.user.entity.PasswordResetAuditEntity;
import com.user.entity.UserEntity;
import com.user.event.PasswordResetRequestedEvent;
import com.user.exceptions.InvalidCredentialsException;
import com.user.exceptions.UserNotFoundException;
import com.user.mapper.UserMapper;
import com.user.producer.UserEventProducer;
import com.user.repository.PasswordResetAuditRepository;
import com.user.repository.UserRepository;
import com.user.util.JwtUtil;
import com.user.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

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
    private final StringRedisTemplate stringRedisTemplate;
    private final PasswordResetAuditRepository passwordResetAuditRepository;

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for CPF: {}", maskCpf(request.getCpf()));

        UserEntity user = userRepository.findByCpf(request.getCpf())
                .orElseThrow(() -> new InvalidCredentialsException("CPF ou senha inválidos"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Senha Invalida para o CPF: {}", maskCpf(request.getCpf()));
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

        log.info("User com CPF {} logado com sucesso", maskCpf(request.getCpf()));

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

        log.info("📤 UserCreatedEvent enviado para userId: {}", user.getId());

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getName()
        );

        log.info("User {} registrado com sucesso", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .message("Usuário registrado com sucesso")
                .build();
    }

    public String requestPasswordReset(String email) {
        log.info("Requisição de Reset de Password enviado para o e-mail : {}", email);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        String resetToken = UUID.randomUUID().toString();
        String redisKey = "password-reset:" + resetToken;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusHours(1);

        stringRedisTemplate.opsForValue().set(
                redisKey,
                user.getId(),
                Duration.ofHours(1)
        );

        PasswordResetAuditEntity audit = PasswordResetAuditEntity.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .token(resetToken)
                .status("REQUESTED")
                .requestedAt(now)
                .expiresAt(expiresAt)
                .build();

        passwordResetAuditRepository.save(audit);

        PasswordResetRequestedEvent event = new PasswordResetRequestedEvent(
                user.getId(),
                user.getEmail(),
                resetToken,
                user.getName()
        );

        userEventProducer.sendPasswordResetEvent(event);

        log.info("📧 Reset de Password enviado para o email: {}", email);

        return "Email de redefinição de senha enviado com sucesso";
    }

    public String resetPassword(String token, String newPassword) {
        String redisKey = "password-reset:" + token;

        String userId = stringRedisTemplate.opsForValue().get(redisKey);

        PasswordResetAuditEntity audit = passwordResetAuditRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Auditoria do token não encontrada"));

        if (userId == null) {
            audit.setStatus("EXPIRED");
            audit.setFailureReason("Token inválido ou expirado");
            passwordResetAuditRepository.save(audit);

            throw new RuntimeException("Token inválido ou expirado");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    audit.setStatus("FAILED");
                    audit.setFailureReason("Usuário não encontrado");
                    passwordResetAuditRepository.save(audit);
                    return new UserNotFoundException("Usuário não encontrado");
                });

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        stringRedisTemplate.delete(redisKey);

        audit.setStatus("COMPLETED");
        audit.setCompletedAt(LocalDateTime.now());
        audit.setPasswordChangedAt(LocalDateTime.now());
        passwordResetAuditRepository.save(audit);

        log.info("✅ Password reset completed for userId: {}", user.getId());

        return "Senha redefinida com sucesso";
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 11) return "***";
        return cpf.substring(0, 3) + ".***.***-" + cpf.substring(9);
    }
}