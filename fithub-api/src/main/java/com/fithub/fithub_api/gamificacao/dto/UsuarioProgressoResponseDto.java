package com.fithub.fithub_api.gamificacao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioProgressoResponseDto {
    private int scoreTotal;
    private int nivel;
    private int xpAtualNoNivel;
    private int xpParaProximoNivel;
    private double percentualProgresso;
    private String tituloNivel;
}
