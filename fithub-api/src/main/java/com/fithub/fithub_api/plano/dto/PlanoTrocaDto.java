package com.fithub.fithub_api.plano.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanoTrocaDto {

    @NotNull(message = "O ID do plano é obrigatório")
    private Long novoPlanoId;
}