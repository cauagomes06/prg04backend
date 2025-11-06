package com.fithub.fithub_api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequestDto {
    @NotBlank(message = "O nome de Usuário (email) não pode ser vazio.")
    private String username;

    @NotBlank(message = " A sennha não pode ser vazia.")
    private String password;
}
