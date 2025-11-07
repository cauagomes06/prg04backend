package com.fithub.fithub_api.notificacao.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificacaoResponseDto {

    private Long id;
    private String mensagem;
    private boolean lida;
    private String link;
    private LocalDateTime dataCriacao;
}
