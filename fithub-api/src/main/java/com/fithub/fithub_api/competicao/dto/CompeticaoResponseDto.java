package com.fithub.fithub_api.competicao.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CompeticaoResponseDto {

    private Long id;
    private String nome;
    private String descricao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    // Campo calculado,sera preenchido no mapper
    private int totalInscritos;
}
