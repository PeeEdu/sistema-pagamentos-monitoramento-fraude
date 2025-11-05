package com.bank_account.mapper;

import com.bank_account.dto.request.CreateUserRequest;
import com.bank_account.dto.response.UserCreateResponse;
import com.bank_account.entities.User;

public class UserMapper {
    public static User toEntity(final CreateUserRequest createUserRequest) {
        return User.builder()
                .name(createUserRequest.name())
                .email(createUserRequest.email())
                .phone(createUserRequest.phone())
                .address(createUserRequest.address())
                .password(createUserRequest.password())
                .build();
    }

    public static UserCreateResponse toResponse(final User user) {
        return UserCreateResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .build();
    }
}
