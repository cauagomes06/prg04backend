package com.fithub.fithub_api.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class UsuarioLoginDto {


    @Email(message = "Email invalido")
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}
