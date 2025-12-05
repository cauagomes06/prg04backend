package com.fithub.fithub_api.pessoa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PessoaCreateDto {

    @NotBlank(message = "O nome completo não pode ser vazio.")
    private String nomeCompleto;

    @Size(min = 11, max = 11, message = "O CPF deve ter exatamente 11 caracteres.")
    @NotBlank
    private String cpf;

    @NotBlank(message = "O telefone não pode ser vazio.")
    private String telefone;
}