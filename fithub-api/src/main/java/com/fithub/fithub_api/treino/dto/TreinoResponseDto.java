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

    // Novos campos de relacionamento
    private Long criadorId;      // Para linkar ao perfil do criador
    private String criadorNome;
    private String criadorFoto;

    private boolean seguindo;    // TRUE se o usuário logado segue, FALSE se não
    private int numeroSeguidores; // Total de seguidores

    private List<ItemTreinoResponseDto> items;

    private Double mediaNota;
    private Integer totalAvaliacoes;
}