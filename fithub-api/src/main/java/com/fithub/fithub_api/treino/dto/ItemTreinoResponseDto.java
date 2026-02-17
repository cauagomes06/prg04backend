package com.fithub.fithub_api.treino.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemTreinoResponseDto {

    private Long id;
    private String series;
    private String repeticoes;
    private String descanso;
    private Integer ordem;

    // --- Campos do exercício ---
    private Long exercicioId;
    private String nomeExercicio;
}