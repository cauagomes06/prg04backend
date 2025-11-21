package com.fithub.fithub_api.notificacao.controller;

import com.fithub.fithub_api.notificacao.dto.NotificacaoBroadcastDto;
import com.fithub.fithub_api.notificacao.dto.NotificacaoResponseDto;
import com.fithub.fithub_api.notificacao.entity.Notificacao;
import com.fithub.fithub_api.notificacao.mapper.NotificacaoMapper;
import com.fithub.fithub_api.notificacao.service.NotificacaoService;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notificacoes")
public class NotificacaoController implements NotificacaoIController{

private final NotificacaoService notificacaoService;
private final UsuarioService usuarioService;


@GetMapping("/minhas")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<List<NotificacaoResponseDto>> minhasNotificacoes
        (@AuthenticationPrincipal UserDetails userDetails){

    Usuario usuarioLogado = getUsuarioLogado(userDetails);

    List<Notificacao> notificacoes = notificacaoService.buscarNotificacoesDoUsuario(usuarioLogado);
    return ResponseEntity.ok().body(NotificacaoMapper.toListNotificacoesDto(notificacoes));
}
    @GetMapping("/contagem-nao-lidas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Integer>> getContagemNaoLidas(
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuarioLogado = getUsuarioLogado(userDetails);
        int contagem = notificacaoService.contarNotificacoesNaoLidas(usuarioLogado);

        // retorna um json simples: "contagem": 5
        return ResponseEntity.ok(Map.of("contagem", contagem));
    }

    @PatchMapping("/{idNotificacao}/ler")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificacaoResponseDto> marcarComoLida(
            @PathVariable Long idNotificacao,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuarioLogado = getUsuarioLogado(userDetails);
        Notificacao notificacao = notificacaoService.marcarComoLida(idNotificacao, usuarioLogado);

        return ResponseEntity.ok(NotificacaoMapper.toNotificacaoDto(notificacao));
    }

    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> enviarParaTodos(@RequestBody @Valid NotificacaoBroadcastDto dto) {
        notificacaoService.enviarParaTodos(dto);
        return ResponseEntity.ok().build();
    }
    // --- Método Auxiliar ---
    private Usuario getUsuarioLogado(UserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("Utilizador não autenticado.");
        }
        return usuarioService.buscarPorUsername(userDetails.getUsername());
    }
}
