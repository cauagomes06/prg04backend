package com.fithub.fithub_api.execucao.dto.item;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemExecucaoCreateDto {

    @NotNull
    private Long exercicioId;

    @NotNull
    private Integer seriesConcluidas;

    private String observacaoExercicio;
}