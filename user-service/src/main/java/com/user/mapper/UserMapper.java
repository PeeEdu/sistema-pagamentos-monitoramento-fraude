package com.user.mapper;

import com.user.dto.request.CreateUserRequest;
import com.user.dto.response.UserCreateResponse;
import com.user.dto.response.UserResponse;
import com.user.entities.UserEntity;
import com.user.event.UserCreatedEvent;
import org.mapstruct.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        imports = {Instant.class, LocalDateTime.class, ZoneId.class}
)
public interface UserMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    UserCreateResponse toCreateResponse(UserEntity userEntity);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "cpf", source = "cpf")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    UserResponse toResponse(UserEntity userEntity);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "createdAt", expression = "java(convertInstantToLocalDateTime(userEntity.getCreatedAt()))")
    UserCreatedEvent toUserCreatedEvent(UserEntity userEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    UserEntity toEntity(CreateUserRequest createUserRequest);

    default LocalDateTime convertInstantToLocalDateTime(Instant instant) {
        if (instant == null) {
            return LocalDateTime.now();
        }
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}