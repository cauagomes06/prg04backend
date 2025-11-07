package com.fithub.fithub_api.treino.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TreinoResponseDto {

    private Long id;
    private String nome;
    private String descricao;
    private String status;
    private List<ItemTreinoResponseDto> items;
}
