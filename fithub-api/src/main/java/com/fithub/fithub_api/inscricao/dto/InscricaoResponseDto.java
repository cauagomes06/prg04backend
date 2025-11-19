package com.fithub.fithub_api.inscricao.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class InscricaoResponseDto {

    private Long idInscricao;
    private Long usuarioId;
    private String nomeUsuario;
    private String resultado;
    private LocalDateTime dataSubmissao;
    private String competicaoNome;
}
