package com.fithub.fithub_api.treino.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemTreinoResponseDto {

    private Long id;
    private int series;
    private String repeticoes;
    private String descanso;
    private int ordem;
    private String nomeExercicio;

}
