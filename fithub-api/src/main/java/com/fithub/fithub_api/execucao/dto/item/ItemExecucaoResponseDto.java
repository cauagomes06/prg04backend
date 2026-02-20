package com.fithub.fithub_api.execucao.dto.item;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemExecucaoResponseDto {

    private Long id;
    private Long exercicioId;
    private String nomeExercicio;
    private Integer seriesConcluidas;
    private String observacaoExercicio;
}
