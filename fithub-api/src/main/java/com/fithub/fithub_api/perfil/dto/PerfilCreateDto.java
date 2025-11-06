package com.fithub.fithub_api.perfil.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PerfilCreateDto {

    @NotBlank(message = "O nome não pode ser vazio.")
    private String nome;
    @NotBlank(message = "A descrição não pode ser vazia.")
    private String descricao;
}
