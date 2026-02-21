package com.fithub.fithub_api.usuario.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UsuarioPerfilPublicoDto {
    private Long id;
    private String nomeCompleto;
    private String username;
    private String fotoUrl;
    private int scoreTotal;
    private int nivel;
    private String tituloNivel;
    private LocalDateTime membroDesde;
}
