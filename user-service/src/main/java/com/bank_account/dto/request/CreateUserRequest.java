package com.bank_account.dto.request;

import com.bank_account.validator.annotation.ValidCPF;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank
        String name,

        @NotBlank
        @Email(message = "Email precisa ser válido")
        String email,

        @NotBlank(message = "A senha não pode estar em branco")
        @Size(min = 8, message = "A senha não pode ser menor que 8 dígitos")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
                message = "A senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial"
        )
        String password,

        @NotBlank(message = "CPF é obrigatório")
        @ValidCPF
        String cpf,

        @NotBlank
        @Pattern(regexp = "^\\+?[1-9][0-9]*$", message = "Telefone precisa ser válido")
        String phone
) {
}
