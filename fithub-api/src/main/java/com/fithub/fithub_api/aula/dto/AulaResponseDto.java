package com.fithub.fithub_api.aula.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AulaResponseDto {

    private Long id;
    private String nome;
    private String descricao;
    private LocalDateTime dataHoraInicio;
    private Integer duracaoMinutos;
    private Integer vagasTotais;


    private Integer vagasDisponiveis;
    private InstrutorResponseDto instrutor;
}
