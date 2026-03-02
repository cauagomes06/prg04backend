package com.fithub.fithub_api.usuario.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRankingDto {

    private int posicao;
    private Long usuarioId;
    private String nomeCompleto;
    private int scoreTotal;
    private String fotoUrl;
}
