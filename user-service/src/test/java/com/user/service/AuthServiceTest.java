package com.user.service;

import com.user.dto.request.LoginRequest;
import com.user.dto.request.RegisterRequest;
import com.user.dto.response.AuthResponse;
import com.user.entities.UserEntity;
import com.user.exceptions.InvalidCredentialsException;
import com.user.mapper.UserMapper;
import com.user.producer.UserEventProducer;
import com.user.repository.UserRepository;
import com.user.stub.LoginRequestStub;
import com.user.stub.RegisterRequestStub;
import com.user.stub.UserCreatedEventStub;
import com.user.stub.UserEntityStub;
import com.user.util.JwtUtil;
import com.user.validator.UserValidator;
import org.junit.jupiter.api.Test;
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
    private final AuthService authService;

    AuthServiceTest() {
        this.userRepository = mock(UserRepository.class);
        this.jwtUtil = mock(JwtUtil.class);
        this.passwordEncoder = mock(PasswordEncoder.class);
        this.userEventProducer = mock(UserEventProducer.class);
        this.userMapper = mock(UserMapper.class);
        this.userValidator = mock(UserValidator.class);

        this.authService = new AuthService(
                userRepository,
                jwtUtil,
                passwordEncoder,
                userEventProducer,
                userMapper,
                userValidator
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
}