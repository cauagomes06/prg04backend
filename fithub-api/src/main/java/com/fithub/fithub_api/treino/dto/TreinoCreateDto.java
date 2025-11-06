package com.fithub.fithub_api.treino.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TreinoCreateDto {

    private String nome;
    private String descricao;
    private List<ItemTreinoCreateDto> items = new ArrayList<>();
}
