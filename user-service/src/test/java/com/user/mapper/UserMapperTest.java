package com.user.mapper;

import com.user.dto.request.RegisterRequest;
import com.user.dto.response.UserCreateResponse;
import com.user.dto.response.UserResponse;
import com.user.entities.UserEntity;
import com.user.event.UserCreatedEvent;
import com.user.stub.RegisterRequestStub;
import com.user.stub.UserEntityStub;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toEntity_DeveRetornarUserEntity_QuandoReceberRegisterRequest() {
        RegisterRequest request = RegisterRequestStub.buildRequest();

        UserEntity response = userMapper.toEntity(request);

        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getPassword(), response.getPassword());
        assertEquals(request.getCpf(), response.getCpf());
        assertEquals(request.getPhone(), response.getPhone());
        assertTrue(response.isActive());
    }

    @Test
    void toCreateResponse_DeveRetornarUserCreateResponse_QuandoReceberUserEntity() {
        UserEntity user = UserEntityStub.buildEntity();

        UserCreateResponse response = userMapper.toCreateResponse(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.id());
        assertEquals(user.getName(), response.name());
        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getPhone(), response.phone());
    }

    @Test
    void toResponse_DeveRetornarUserResponse_QuandoReceberUserEntity() {
        Instant createdAt = Instant.parse("2024-01-01T10:00:00Z");
        Instant updatedAt = Instant.parse("2024-01-02T10:00:00Z");

        UserEntity user = UserEntityStub.buildEntityWithDates(createdAt, updatedAt);

        UserResponse response = userMapper.toResponse(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.id());
        assertEquals(user.getName(), response.name());
        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getPassword(), response.password());
        assertEquals(user.getCpf(), response.cpf());
        assertEquals(user.getPhone(), response.phone());
        assertEquals(createdAt, response.createdAt());
        assertEquals(updatedAt, response.updatedAt());
    }

    @Test
    void toUserCreatedEvent_DeveRetornarUserCreatedEventComCreatedAtConvertido_QuandoInstantNaoForNulo() {
        Instant createdAt = Instant.parse("2024-01-01T10:00:00Z");
        UserEntity user = UserEntityStub.buildEntityWithDates(createdAt, Instant.parse("2024-01-02T10:00:00Z"));

        UserCreatedEvent response = userMapper.toUserCreatedEvent(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.getUserId());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(
                LocalDateTime.ofInstant(createdAt, ZoneId.systemDefault()),
                response.getCreatedAt()
        );
    }

    @Test
    void toUserCreatedEvent_DeveRetornarUserCreatedEventComCreatedAtAtual_QuandoInstantForNulo() {
        UserEntity user = UserEntityStub.buildEntityWithDates(null, Instant.parse("2024-01-02T10:00:00Z"));

        LocalDateTime before = LocalDateTime.now();

        UserCreatedEvent response = userMapper.toUserCreatedEvent(user);

        LocalDateTime after = LocalDateTime.now();

        assertNotNull(response);
        assertEquals(user.getId(), response.getUserId());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
        assertNotNull(response.getCreatedAt());
        assertFalse(response.getCreatedAt().isBefore(before));
        assertFalse(response.getCreatedAt().isAfter(after));
    }

    @Test
    void toEntity_DeveRetornarNulo_QuandoRegisterRequestForNulo() {
        UserEntity response = userMapper.toEntity(null);

        assertNull(response);
    }

    @Test
    void toCreateResponse_DeveRetornarNulo_QuandoUserEntityForNulo() {
        UserCreateResponse response = userMapper.toCreateResponse(null);

        assertNull(response);
    }

    @Test
    void toResponse_DeveRetornarNulo_QuandoUserEntityForNulo() {
        UserResponse response = userMapper.toResponse(null);

        assertNull(response);
    }

    @Test
    void toUserCreatedEvent_DeveRetornarNulo_QuandoUserEntityForNulo() {
        UserCreatedEvent response = userMapper.toUserCreatedEvent(null);

        assertNull(response);
    }

    @Test
    void convertInstantToLocalDateTime_DeveRetornarDataConvertida_QuandoInstantNaoForNulo() {
        Instant instant = Instant.parse("2024-01-01T10:00:00Z");

        LocalDateTime response = userMapper.convertInstantToLocalDateTime(instant);

        assertEquals(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()), response);
    }

    @Test
    void convertInstantToLocalDateTime_DeveRetornarDataAtual_QuandoInstantForNulo() {
        LocalDateTime before = LocalDateTime.now();

        LocalDateTime response = userMapper.convertInstantToLocalDateTime(null);

        LocalDateTime after = LocalDateTime.now();

        assertNotNull(response);
        assertFalse(response.isBefore(before));
        assertFalse(response.isAfter(after));
    }
}