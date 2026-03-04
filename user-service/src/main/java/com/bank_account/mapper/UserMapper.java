package com.bank_account.mapper;

import com.bank_account.dto.request.CreateUserRequest;
import com.bank_account.dto.response.UserCreateResponse;
import com.bank_account.dto.response.UserResponse;
import com.bank_account.entities.UserEntity;
import com.bank_account.event.UserCreatedEvent;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
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
    @Mapping(target = "cpf", source = "cpf")
    UserCreatedEvent toUserCreatedEvent(UserEntity userEntity);

    UserEntity toEntity(CreateUserRequest createUserRequest);

}
