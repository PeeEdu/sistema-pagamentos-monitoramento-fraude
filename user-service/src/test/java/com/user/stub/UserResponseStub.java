package com.user.stub;

import com.user.dto.response.UserResponse;

import java.util.List;

public class UserResponseStub {

    private UserResponseStub() {
    }

    public static UserResponse buildResponse() {
        return UserResponse.builder()
                .id("507f1f77bcf86cd799439011")
                .name("João Silva")
                .email("joao.silva@email.com")
                .build();
    }

    public static UserResponse buildResponse(String id) {
        return UserResponse.builder()
                .id(id)
                .name("João Silva")
                .email("joao.silva@email.com")
                .build();
    }

    public static UserResponse buildResponse(String id, String name, String email) {
        return UserResponse.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    public static List<UserResponse> buildResponseList() {
        return List.of(
                buildResponse("507f1f77bcf86cd799439011", "João Silva", "joao.silva@email.com"),
                buildResponse("507f1f77bcf86cd799439012", "Maria Souza", "maria.souza@email.com")
        );
    }

    public static List<UserResponse> buildEmptyResponseList() {
        return List.of();
    }
}