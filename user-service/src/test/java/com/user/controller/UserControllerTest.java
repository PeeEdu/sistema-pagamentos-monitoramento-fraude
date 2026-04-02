package com.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.service.UserService;
import com.user.stub.UserResponseStub;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class UserControllerTest {

    private static final String BASE_URL = "/api/user";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final UserService userService;
    private final MockMvc mockMvc;

    UserControllerTest() {
        this.userService = mock(UserService.class);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new UserController(userService))
                .build();
    }

    @Test
    void findAll_DeveRetornarStatus200ComListaDeUsuarios() throws Exception {
        var response = UserResponseStub.buildResponseList();

        when(userService.findAll()).thenReturn(response);

        mockMvc.perform(
                        get(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Usuários encontrados com sucesso")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id", is("507f1f77bcf86cd799439011")))
                .andExpect(jsonPath("$.data[0].name", is("João Silva")))
                .andExpect(jsonPath("$.data[0].email", is("joao.silva@email.com")))
                .andExpect(jsonPath("$.data[1].id", is("507f1f77bcf86cd799439012")))
                .andExpect(jsonPath("$.data[1].name", is("Maria Souza")))
                .andExpect(jsonPath("$.data[1].email", is("maria.souza@email.com")));

        verify(userService).findAll();
    }

    @Test
    void findAll_DeveRetornarStatus200ComListaVazia_QuandoNaoExistiremUsuariosCadastrados() throws Exception {
        var response = UserResponseStub.buildEmptyResponseList();

        when(userService.findAll()).thenReturn(response);

        mockMvc.perform(
                        get(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Usuários encontrados com sucesso")))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(userService).findAll();
    }

    @Test
    void getUserById_DeveRetornarStatus200ComUsuario_QuandoIdExistir() throws Exception {
        var response = UserResponseStub.buildResponse();

        when(userService.findUserById("507f1f77bcf86cd799439011")).thenReturn(response);

        mockMvc.perform(
                        get(BASE_URL + "/{id}", "507f1f77bcf86cd799439011")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Usuário encontrado com sucesso")))
                .andExpect(jsonPath("$.data.id", is(response.id())))
                .andExpect(jsonPath("$.data.name", is(response.name())))
                .andExpect(jsonPath("$.data.email", is(response.email())));

        verify(userService).findUserById("507f1f77bcf86cd799439011");
    }

    @Test
    void getUserById_DeveRetornarStatus200ComUsuario_QuandoIdForDiferenteDoPadraoObjectId() throws Exception {
        var id = "user-123";
        var response = UserResponseStub.buildResponse(id, "Ana Paula", "ana.paula@email.com");

        when(userService.findUserById(id)).thenReturn(response);

        mockMvc.perform(
                        get(BASE_URL + "/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Usuário encontrado com sucesso")))
                .andExpect(jsonPath("$.data.id", is(response.id())))
                .andExpect(jsonPath("$.data.name", is(response.name())))
                .andExpect(jsonPath("$.data.email", is(response.email())));

        verify(userService).findUserById(id);
    }
}