package com.bank_account.controller;

import com.bank_account.dto.request.CreateUserRequest;
import com.bank_account.dto.response.BaseResponse;
import com.bank_account.dto.response.UserCreateResponse;
import com.bank_account.dto.response.UserResponse;
import com.bank_account.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Tag(name = "Users", description = "Endpoints para gerenciamento de usuários")
public record UserController(
        UserService userService
) {
    @PostMapping()
    @Operation(
            summary = "Criar usuário",
            description = "Cria um novo usuário no sistema bancário e envia evento para Kafka"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Usuário já existe"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<BaseResponse<UserCreateResponse>> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do usuário a ser criado",
                    required = true
            )
            @RequestBody @Valid CreateUserRequest createUserRequest
    ) {
        final UserCreateResponse response = userService.createUser(createUserRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.<UserCreateResponse>builder()
                        .data(response)
                        .message("Usuário criado com sucesso")
                        .build());
    }

    @GetMapping
    @Operation(
            summary = "Listar todos os usuários",
            description = "Retorna uma lista com todos os usuários cadastrados no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<BaseResponse<List<UserResponse>>> findAll(){
        final var users = userService.findAll();

        return ResponseEntity.ok(BaseResponse.<List<UserResponse>>builder()
                .data(users)
                .message("Usuários encontrados com sucesso")
                .build());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar usuário por ID",
            description = "Retorna os dados de um usuário específico pelo seu ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<BaseResponse<UserResponse>> getUserById(
            @Parameter(description = "ID do usuário", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable("id") String id
    ){
        final var user = userService.findUserById(id);

        return ResponseEntity.ok(BaseResponse.<UserResponse>builder()
                .data(user)
                .message("Usuário encontrado com sucesso")
                .build());
    }
}