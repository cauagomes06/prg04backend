package com.fithub.fithub_api.pessoa.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PessoaCreateDto {

    @NotBlank(message = "O nome completo não pode ser vazio.")
    private String nomeCompleto;

    @NotBlank(message = "O CPF não pode ser vazio.")
    private String cpf;

    @NotBlank(message = "O telefone não pode ser vazio.")
    private String telefone;
}