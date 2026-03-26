package com.user.workflow.mapper;

import com.user.dto.request.RegisterRequest;
import com.user.entities.UserEntity;
import com.user.workflow.dto.UserData;
import com.user.workflow.dto.UserRegistrationData;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ProcessVariableMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "cpf", source = "cpf")
    @Mapping(target = "phone", source = "phone")
    UserRegistrationData toProcessVariable(RegisterRequest request);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    UserData toProcessVariable(UserEntity user);
}