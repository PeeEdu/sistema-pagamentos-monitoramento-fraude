package com.user.workflow;

import com.user.dto.request.RegisterRequest;
import com.user.entities.UserEntity;
import com.user.workflow.dto.UserData;
import com.user.workflow.dto.UserRegistrationData;
import com.user.workflow.mapper.ProcessVariableMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class ProcessVariableMapperTest {

    private final ProcessVariableMapper processVariableMapper =
            Mappers.getMapper(ProcessVariableMapper.class);

    @Test
    void toProcessVariable_DeveRetornarUserRegistrationData_QuandoReceberRegisterRequest() {
        RegisterRequest request = RegisterRequest.builder()
                .name("João Silva")
                .email("joao.silva@email.com")
                .password("123456")
                .cpf("52998224725")
                .phone("11999999999")
                .build();

        UserRegistrationData response = processVariableMapper.toProcessVariable(request);

        assertNotNull(response);
        assertEquals("João Silva", response.getName());
        assertEquals("joao.silva@email.com", response.getEmail());
        assertEquals("123456", response.getPassword());
        assertEquals("52998224725", response.getCpf());
        assertEquals("11999999999", response.getPhone());
    }

    @Test
    void toProcessVariable_DeveRetornarUserData_QuandoReceberUserEntity() {
        UserEntity user = UserEntity.builder()
                .id("user-123")
                .name("João Silva")
                .email("joao.silva@email.com")
                .password("$2a$10$passwordEncoded")
                .cpf("52998224725")
                .phone("11999999999")
                .active(true)
                .build();

        UserData response = processVariableMapper.toProcessVariable(user);

        assertNotNull(response);
        assertEquals("user-123", response.getId());
        assertEquals("João Silva", response.getName());
        assertEquals("joao.silva@email.com", response.getEmail());
    }

    @Test
    void toProcessVariable_DeveRetornarNulo_QuandoRegisterRequestForNulo() {
        UserRegistrationData response = processVariableMapper.toProcessVariable((RegisterRequest) null);

        assertNull(response);
    }

    @Test
    void toProcessVariable_DeveRetornarNulo_QuandoUserEntityForNulo() {
        UserData response = processVariableMapper.toProcessVariable((UserEntity) null);

        assertNull(response);
    }
}