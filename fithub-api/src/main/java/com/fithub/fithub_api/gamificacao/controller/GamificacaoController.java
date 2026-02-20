package com.fithub.fithub_api.gamificacao.controller;


import com.fithub.fithub_api.gamificacao.dto.UsuarioProgressoResponseDto;
import com.fithub.fithub_api.gamificacao.service.GamificacaoIService;
import com.fithub.fithub_api.infraestructure.SecurityUtils;
import com.fithub.fithub_api.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/gamificacao")
public class GamificacaoController {

    private final GamificacaoIService gamificacaoService;
    private final SecurityUtils  securityUtils;

    @GetMapping("/meu-progresso")
    public ResponseEntity<UsuarioProgressoResponseDto> getMeuProgresso() {
        Usuario usuario = securityUtils.getUsuarioLogado();
        UsuarioProgressoResponseDto progresso = gamificacaoService.calcularProgresso(usuario.getScoreTotal());
        return ResponseEntity.ok(progresso);
    }
}
