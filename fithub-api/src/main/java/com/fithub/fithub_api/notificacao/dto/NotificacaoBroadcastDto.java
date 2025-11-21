package com.fithub.fithub_api.notificacao.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificacaoBroadcastDto {

    //novo dto para envio de notificacoes para todos
    @NotBlank(message = "A mensagem não pode ser vazia.")
    private String mensagem;

    private String link; //  (ex: "/portal/competicoes")
}