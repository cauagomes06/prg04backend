package com.fithub.fithub_api.notificacao.controller;

import com.fithub.fithub_api.notificacao.dto.NotificacaoResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

public interface NotificacaoIController{

    ResponseEntity<List<NotificacaoResponseDto>> minhasNotificacoes
            (@AuthenticationPrincipal UserDetails userDetails);

    ResponseEntity<Map<String, Integer>> getContagemNaoLidas(
            @AuthenticationPrincipal UserDetails userDetails);

    ResponseEntity<NotificacaoResponseDto> marcarComoLida(
            @PathVariable Long idNotificacao,
            @AuthenticationPrincipal UserDetails userDetails);
}
