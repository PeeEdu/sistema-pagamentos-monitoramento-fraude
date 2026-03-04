package com.bank_account.controller;

import com.bank_account.dto.request.CreateUserRequest;
import com.bank_account.dto.response.BaseResponse;
import com.bank_account.dto.response.UserCreateResponse;
import com.bank_account.dto.response.UserResponse;
import com.bank_account.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public record UserController(
        UserService userService
) {
    @PostMapping()
    @Operation(
            summary = "Criar um usuário",
            description = "Cria um novo usuário no sistema bancário"
    )
    public ResponseEntity<BaseResponse<UserCreateResponse>> createUser(
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
            summary = "Lista todos os usuários",
            description = "Lista todos os usuários cadastrados do sistema bancário"
    )
    public ResponseEntity<BaseResponse<List<UserResponse>>> findAll(){
        final var users = userService.findAll();

        return ResponseEntity.ok(BaseResponse.<List<UserResponse>>builder()
                .data(users)
                .message("Usuários encontrados com sucesso")
                .build());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lista um usuário por ID",
            description = "Lista um usuário pelo ID fornecido"
    )
    public ResponseEntity<BaseResponse<UserResponse>> getUserById(@PathVariable("id") String id){
        final var user = userService.findUserById(id);

        return ResponseEntity.ok(BaseResponse.<UserResponse>builder()
                .data(user)
                .message("Usuário encontrado com sucesso")
                .build());
    }
}

