package com.fithub.fithub_api.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioCreateDto {

    @NotBlank
    @Email(message = "Formato do e-mail está inválido")
    private String username;

    @NotBlank
    @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres")
    private String password;

    @NotNull(message = "O ID do perfil não pode ser nulo.")
    private Long perfil; // id do perfil do usuario

    @NotNull(message = "O ID do plano não pode ser nulo.")
    private Long plano; // id do plano do usuario


    @NotNull(message = "Os dados da pessoa não podem ser nulos.")
    @Valid
    private PessoaCreateDto pessoa;
}