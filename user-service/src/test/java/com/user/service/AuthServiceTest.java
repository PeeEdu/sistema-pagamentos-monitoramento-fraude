package com.user.service;

import com.user.dto.request.LoginRequest;
import com.user.dto.request.RegisterRequest;
import com.user.dto.response.AuthResponse;
import com.user.entity.PasswordResetAuditEntity;
import com.user.entity.UserEntity;
import com.user.exceptions.InvalidCredentialsException;
import com.user.exceptions.UserNotFoundException;
import com.user.mapper.UserMapper;
import com.user.producer.UserEventProducer;
import com.user.repository.PasswordResetAuditRepository;
import com.user.repository.UserRepository;
import com.user.stub.*;
import com.user.util.JwtUtil;
import com.user.validator.UserValidator;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserEventProducer userEventProducer;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final StringRedisTemplate stringRedisTemplate;
    private final PasswordResetAuditRepository passwordResetAuditRepository;
    private final ValueOperations<String, String> valueOperations;
    private final AuthService authService;

    AuthServiceTest() {
        this.userRepository = mock(UserRepository.class);
        this.jwtUtil = mock(JwtUtil.class);
        this.passwordEncoder = mock(PasswordEncoder.class);
        this.userEventProducer = mock(UserEventProducer.class);
        this.userMapper = mock(UserMapper.class);
        this.userValidator = mock(UserValidator.class);
        this.stringRedisTemplate = mock(StringRedisTemplate.class);
        this.passwordResetAuditRepository = mock(PasswordResetAuditRepository.class);
        this.valueOperations = mock(ValueOperations.class);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        this.authService = new AuthService(
                userRepository,
                jwtUtil,
                passwordEncoder,
                userEventProducer,
                userMapper,
                userValidator,
                stringRedisTemplate,
                passwordResetAuditRepository
        );
    }


    @Test
    void login_DeveRetornarAuthResponse_QuandoCredenciaisForemValidas() {
        LoginRequest request = LoginRequestStub.buildRequest();
        UserEntity user = UserEntityStub.buildEntity();

        when(userRepository.findByCpf(request.getCpf())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getId(), user.getEmail(), user.getName())).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals("Login realizado com sucesso", response.getMessage());

        verify(userRepository).findByCpf(request.getCpf());
        verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
        verify(jwtUtil).generateToken(user.getId(), user.getEmail(), user.getName());
    }

    @Test
    void login_DeveLancarInvalidCredentialsException_QuandoCpfNaoExistir() {
        LoginRequest request = LoginRequestStub.buildRequest();

        when(userRepository.findByCpf(request.getCpf())).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("CPF ou senha inválidos", exception.getMessage());

        verify(userRepository).findByCpf(request.getCpf());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString(), anyString());
    }

    @Test
    void login_DeveLancarInvalidCredentialsException_QuandoSenhaForInvalida() {
        LoginRequest request = LoginRequestStub.buildRequest();
        UserEntity user = UserEntityStub.buildEntity();

        when(userRepository.findByCpf(request.getCpf())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("CPF ou senha inválidos", exception.getMessage());

        verify(userRepository).findByCpf(request.getCpf());
        verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
        verify(jwtUtil, never()).generateToken(anyString(), anyString(), anyString());
    }

    @Test
    void login_DeveLancarInvalidCredentialsException_QuandoUsuarioEstiverInativo() {
        LoginRequest request = LoginRequestStub.buildRequest();
        UserEntity user = UserEntityStub.buildInactiveEntity();

        when(userRepository.findByCpf(request.getCpf())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("Usuário inativo", exception.getMessage());

        verify(userRepository).findByCpf(request.getCpf());
        verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
        verify(jwtUtil, never()).generateToken(anyString(), anyString(), anyString());
    }

    @Test
    void register_DeveRetornarAuthResponse_QuandoDadosForemValidos() {
        RegisterRequest request = RegisterRequestStub.buildRequest();
        UserEntity savedUser = UserEntityStub.buildEntity();
        var userCreatedEvent = UserCreatedEventStub.buildEvent();

        when(passwordEncoder.encode(request.getPassword())).thenReturn("$2a$10$passwordEncoded");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
        when(userMapper.toUserCreatedEvent(savedUser)).thenReturn(userCreatedEvent);
        when(jwtUtil.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getName()))
                .thenReturn("mocked-jwt-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals(savedUser.getId(), response.getId());
        assertEquals(savedUser.getName(), response.getName());
        assertEquals(savedUser.getEmail(), response.getEmail());
        assertEquals("Usuário registrado com sucesso", response.getMessage());

        verify(userValidator).validateNewUser(
                request.getEmail(),
                request.getCpf(),
                request.getPhone()
        );
        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(any(UserEntity.class));
        verify(userMapper).toUserCreatedEvent(savedUser);
        verify(userEventProducer).sendUserCreatedEvent(userCreatedEvent);
        verify(jwtUtil).generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getName());
    }

    @Test
    void register_DeveSalvarUsuarioComSenhaCriptografada_QuandoCadastrarNovoUsuario() {
        RegisterRequest request = RegisterRequestStub.buildRequest();
        UserEntity savedUser = UserEntityStub.buildEntity();
        var userCreatedEvent = UserCreatedEventStub.buildEvent();

        when(passwordEncoder.encode(request.getPassword())).thenReturn("senha-criptografada");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
        when(userMapper.toUserCreatedEvent(savedUser)).thenReturn(userCreatedEvent);
        when(jwtUtil.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getName()))
                .thenReturn("mocked-jwt-token");

        authService.register(request);

        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(argThat(user ->
                user.getName().equals(request.getName()) &&
                        user.getEmail().equals(request.getEmail()) &&
                        user.getCpf().equals(request.getCpf()) &&
                        user.getPhone().equals(request.getPhone()) &&
                        user.getPassword().equals("senha-criptografada") &&
                        user.isActive()
        ));
    }

    @Test
    void register_DeveChamarUserValidator_QuandoIniciarCadastro() {
        RegisterRequest request = RegisterRequestStub.buildRequest();
        UserEntity savedUser = UserEntityStub.buildEntity();
        var userCreatedEvent = UserCreatedEventStub.buildEvent();

        when(passwordEncoder.encode(request.getPassword())).thenReturn("$2a$10$passwordEncoded");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
        when(userMapper.toUserCreatedEvent(savedUser)).thenReturn(userCreatedEvent);
        when(jwtUtil.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getName()))
                .thenReturn("mocked-jwt-token");

        authService.register(request);

        verify(userValidator).validateNewUser(
                request.getEmail(),
                request.getCpf(),
                request.getPhone()
        );
    }

    @Test
    void register_DevePropagarExcecao_QuandoUserValidatorRejeitarNovoUsuario() {
        RegisterRequest request = RegisterRequestStub.buildRequest();

        doThrow(new IllegalArgumentException("Usuário já cadastrado"))
                .when(userValidator)
                .validateNewUser(request.getEmail(), request.getCpf(), request.getPhone());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertEquals("Usuário já cadastrado", exception.getMessage());

        verify(userValidator).validateNewUser(
                request.getEmail(),
                request.getCpf(),
                request.getPhone()
        );
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(userEventProducer, never()).sendUserCreatedEvent(any());
        verify(jwtUtil, never()).generateToken(anyString(), anyString(), anyString());
    }

    @Test
    void validateToken_DeveRetornarTrue_QuandoTokenForValido() {
        when(jwtUtil.validateToken("mocked-jwt-token")).thenReturn(true);

        boolean response = authService.validateToken("mocked-jwt-token");

        assertTrue(response);
        verify(jwtUtil).validateToken("mocked-jwt-token");
    }

    @Test
    void validateToken_DeveRetornarFalse_QuandoTokenForInvalido() {
        when(jwtUtil.validateToken("mocked-jwt-token")).thenReturn(false);

        boolean response = authService.validateToken("mocked-jwt-token");

        assertFalse(response);
        verify(jwtUtil).validateToken("mocked-jwt-token");
    }
    @Test
    void requestPasswordReset_DeveRetornarMensagemDeSucesso_QuandoEmailExistir() {
        UserEntity user = UserEntityStub.buildEntity();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        String response = authService.requestPasswordReset(user.getEmail());

        assertEquals("Email de redefinição de senha enviado com sucesso", response);

        verify(userRepository).findByEmail(user.getEmail());
        verify(stringRedisTemplate).opsForValue();
        verify(valueOperations).set(startsWith("password-reset:"), eq(user.getId()), eq(java.time.Duration.ofHours(1)));
        verify(passwordResetAuditRepository).save(argThat(audit ->
                audit.getUserId().equals(user.getId()) &&
                        audit.getEmail().equals(user.getEmail()) &&
                        audit.getStatus().equals("REQUESTED") &&
                        audit.getToken() != null &&
                        audit.getRequestedAt() != null &&
                        audit.getExpiresAt() != null
        ));
        verify(userEventProducer).sendPasswordResetEvent(argThat(event ->
                event.getUserId().equals(user.getId()) &&
                        event.getEmail().equals(user.getEmail()) &&
                        event.getUserName().equals(user.getName()) &&
                        event.getResetToken() != null
        ));
    }

    @Test
    void requestPasswordReset_DeveLancarUserNotFoundException_QuandoEmailNaoExistir() {
        when(userRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> authService.requestPasswordReset("inexistente@email.com")
        );

        assertEquals("Usuário com ID 'Usuário não encontrado' não encontrado", exception.getMessage());

        verify(userRepository).findByEmail("inexistente@email.com");
        verify(passwordResetAuditRepository, never()).save(any());
        verify(userEventProducer, never()).sendPasswordResetEvent(any());
    }

    @Test
    void resetPassword_DeveRedefinirSenhaComSucesso_QuandoTokenEUsuarioForemValidos() {
        String token = "reset-token-123";
        String redisKey = "password-reset:" + token;
        String userId = "user-123";
        String newPassword = "novaSenha123";

        UserEntity user = UserEntityStub.buildEntity();
        PasswordResetAuditEntity audit = PasswordResetAuditEntityStub.buildEntity();

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(userId);
        when(passwordResetAuditRepository.findByToken(token)).thenReturn(Optional.of(audit));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("senha-criptografada");

        String response = authService.resetPassword(token, newPassword);

        assertEquals("Senha redefinida com sucesso", response);
        assertEquals("senha-criptografada", user.getPassword());
        assertNotNull(user.getUpdatedAt());

        verify(stringRedisTemplate).opsForValue();
        verify(valueOperations).get(redisKey);
        verify(passwordResetAuditRepository).findByToken(token);
        verify(userRepository).findById(userId);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(user);
        verify(stringRedisTemplate).delete(redisKey);
        verify(passwordResetAuditRepository, times(1)).save(argThat(savedAudit ->
                savedAudit.getStatus().equals("COMPLETED") &&
                        savedAudit.getCompletedAt() != null &&
                        savedAudit.getPasswordChangedAt() != null
        ));
    }

    @Test
    void resetPassword_DeveLancarRuntimeException_QuandoAuditoriaNaoExistir() {
        String token = "reset-token-123";
        String redisKey = "password-reset:" + token;

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn("user-123");
        when(passwordResetAuditRepository.findByToken(token)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.resetPassword(token, "novaSenha123")
        );

        assertEquals("Auditoria do token não encontrada", exception.getMessage());

        verify(valueOperations).get(redisKey);
        verify(passwordResetAuditRepository).findByToken(token);
        verify(userRepository, never()).findById(anyString());
    }

    @Test
    void resetPassword_DeveLancarRuntimeExceptionEAtualizarAuditoria_QuandoTokenForInvalidoOuExpirado() {
        String token = "reset-token-123";
        String redisKey = "password-reset:" + token;

        PasswordResetAuditEntity audit = PasswordResetAuditEntityStub.buildEntity();

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(null);
        when(passwordResetAuditRepository.findByToken(token)).thenReturn(Optional.of(audit));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.resetPassword(token, "novaSenha123")
        );

        assertEquals("Token inválido ou expirado", exception.getMessage());

        verify(passwordResetAuditRepository).save(argThat(savedAudit ->
                savedAudit.getStatus().equals("EXPIRED") &&
                        savedAudit.getFailureReason().equals("Token inválido ou expirado")
        ));
        verify(userRepository, never()).findById(anyString());
        verify(stringRedisTemplate, never()).delete(anyString());
    }

    @Test
    void resetPassword_DeveLancarUserNotFoundExceptionEAtualizarAuditoria_QuandoUsuarioNaoExistir() {
        String token = "reset-token-123";
        String redisKey = "password-reset:" + token;
        String userId = "user-123";

        PasswordResetAuditEntity audit = PasswordResetAuditEntityStub.buildEntity();

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(redisKey)).thenReturn(userId);
        when(passwordResetAuditRepository.findByToken(token)).thenReturn(Optional.of(audit));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> authService.resetPassword(token, "novaSenha123")
        );

        assertEquals("Usuário com ID 'Usuário não encontrado' não encontrado", exception.getMessage());
        
        verify(passwordResetAuditRepository).save(argThat(savedAudit ->
                savedAudit.getStatus().equals("FAILED") &&
                        savedAudit.getFailureReason().equals("Usuário não encontrado")
        ));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
        verify(stringRedisTemplate, never()).delete(anyString());
    }

    @Test
    void login_DeveLancarInvalidCredentialsException_QuandoCpfForNulo() {
        LoginRequest request = mock(LoginRequest.class);

        when(request.getCpf()).thenReturn(null);
        when(userRepository.findByCpf(null)).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("CPF ou senha inválidos", exception.getMessage());

        verify(userRepository).findByCpf(null);
    }

    @Test
    void login_DeveLancarInvalidCredentialsException_QuandoCpfForCurto() {
        LoginRequest request = mock(LoginRequest.class);

        when(request.getCpf()).thenReturn("123");
        when(userRepository.findByCpf("123")).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("CPF ou senha inválidos", exception.getMessage());

        verify(userRepository).findByCpf("123");
    }
}