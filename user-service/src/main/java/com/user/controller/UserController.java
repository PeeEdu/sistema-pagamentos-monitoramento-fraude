package com.user.controller;

import com.user.dto.response.BaseResponse;
import com.user.dto.response.UserResponse;
import com.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "Endpoints para gerenciamento de usuários")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(
            summary = "Listar todos os usuários",
            description = "Retorna uma lista com todos os usuários cadastrados no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado - Token inválido ou ausente"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<List<UserResponse>>> findAll() {
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
            @ApiResponse(responseCode = "401", description = "Não autorizado - Token inválido ou ausente"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<UserResponse>> getUserById(
            @Parameter(description = "ID do usuário", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable("id") String id
    ) {
        final var user = userService.findUserById(id);

        return ResponseEntity.ok(BaseResponse.<UserResponse>builder()
                .data(user)
                .message("Usuário encontrado com sucesso")
                .build());
    }
}