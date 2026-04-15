package com.user.service;

import com.user.dto.response.UserResponse;
import com.user.entity.UserEntity;
import com.user.exceptions.UserNotFoundException;
import com.user.mapper.UserMapper;
import com.user.repository.UserRepository;
import com.user.stub.UserEntityStub;
import com.user.stub.UserResponseStub;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;

    UserServiceTest() {
        this.userRepository = mock(UserRepository.class);
        this.userMapper = mock(UserMapper.class);
        this.userService = new UserService(userRepository, userMapper);
    }

    @Test
    void findAll_DeveRetornarListaDeUsuarios_QuandoExistiremUsuariosCadastrados() {
        UserEntity user1 = UserEntityStub.buildEntity();
        UserEntity user2 = UserEntity.builder()
                .id("user-456")
                .name("Maria Souza")
                .email("maria.souza@email.com")
                .password("$2a$10$passwordEncoded")
                .cpf("12345678909")
                .phone("11988888888")
                .active(true)
                .build();

        UserResponse response1 = UserResponseStub.buildResponse(
                "user-123",
                "João Silva",
                "joao.silva@email.com"
        );

        UserResponse response2 = UserResponseStub.buildResponse(
                "user-456",
                "Maria Souza",
                "maria.souza@email.com"
        );

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toResponse(user1)).thenReturn(response1);
        when(userMapper.toResponse(user2)).thenReturn(response2);

        List<UserResponse> response = userService.findAll();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("user-123", response.getFirst().id());
        assertEquals("João Silva", response.get(0).name());
        assertEquals("joao.silva@email.com", response.get(0).email());
        assertEquals("user-456", response.get(1).id());
        assertEquals("Maria Souza", response.get(1).name());
        assertEquals("maria.souza@email.com", response.get(1).email());

        verify(userRepository).findAll();
        verify(userMapper).toResponse(user1);
        verify(userMapper).toResponse(user2);
    }

    @Test
    void findAll_DeveRetornarListaVazia_QuandoNaoExistiremUsuariosCadastrados() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponse> response = userService.findAll();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(userRepository).findAll();
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    void findUserById_DeveRetornarUsuario_QuandoIdExistir() {
        String id = "user-123";
        UserEntity user = UserEntityStub.buildEntity();
        UserResponse userResponse = UserResponseStub.buildResponse(
                "user-123",
                "João Silva",
                "joao.silva@email.com"
        );

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse response = userService.findUserById(id);

        assertNotNull(response);
        assertEquals("user-123", response.id());
        assertEquals("João Silva", response.name());
        assertEquals("joao.silva@email.com", response.email());

        verify(userRepository).findById(id);
        verify(userMapper).toResponse(user);
    }

    @Test
    void findUserById_DeveLancarUserNotFoundException_QuandoIdNaoExistir() {
        String id = "user-inexistente";

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.findUserById(id)
        );

        assertEquals("Usuário com ID 'User not found with id: " + id + "' não encontrado", exception.getMessage());

        verify(userRepository).findById(id);
        verify(userMapper, never()).toResponse(any());
    }
}