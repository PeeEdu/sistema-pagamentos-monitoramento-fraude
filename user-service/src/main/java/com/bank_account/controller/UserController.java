package com.bank_account.controller;

import com.bank_account.dto.request.CreateUserRequest;
import com.bank_account.dto.response.BaseResponse;
import com.bank_account.dto.response.UserCreateResponse;
import com.bank_account.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public record UserController(
        UserService userService
) {
    @PostMapping("/create")
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
}

