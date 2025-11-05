package com.bank_account.dto.request;

import com.bank_account.entities.vo.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateUserRequest(
        @NotBlank
        String name,

        @NotBlank
        @Email(message = "Email precisa ser válido")
        String email,

        @NotBlank
        @Pattern(regexp = "^\\+?[1-9][0-9]*$", message = "Telefone precisa ser válido")
        String phone,

        @Valid
        Address address,

        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
                message = "Senha precisa ter pelo menos 8 caracteres, uma letra e um número"
        )
        String password
) {
}
