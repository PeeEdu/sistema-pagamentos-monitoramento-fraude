package com.user.service;

import com.user.dto.response.UserResponse;
import com.user.entities.UserEntity;
import com.user.exceptions.UserNotFoundException;
import com.user.mapper.UserMapper;
import com.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> findAll(){
        final List<UserEntity> userEntity = userRepository.findAll();

        return userEntity.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse findUserById(final String id) {
        Optional<UserEntity> userEntity = userRepository.findById(id);

        return userEntity
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
}
