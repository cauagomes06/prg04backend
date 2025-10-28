package com.fithub.fithub_api.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemTreinoCreateDto {

    private Long exercicioId;
    private String repeticoes;
    private String series;
    private String descanso;
    private int ordem; // para ordenar os exercicios
}
