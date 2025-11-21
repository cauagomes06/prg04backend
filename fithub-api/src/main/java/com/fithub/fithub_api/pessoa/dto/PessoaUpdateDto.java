package com.fithub.fithub_api.pessoa.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


    @Getter
    @Setter
    public class PessoaUpdateDto {

        @NotBlank(message = "O nome completo não pode ser vazio.")
        private String nomeCompleto;

        @NotBlank(message = "O telefone não pode ser vazio.")
        private String telefone;
    }

