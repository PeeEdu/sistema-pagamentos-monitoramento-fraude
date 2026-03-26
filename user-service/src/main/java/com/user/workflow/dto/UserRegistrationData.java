package com.user.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationData implements Serializable {
    private String name;
    private String email;
    private String password;
    private String cpf;
    private String phone;
}