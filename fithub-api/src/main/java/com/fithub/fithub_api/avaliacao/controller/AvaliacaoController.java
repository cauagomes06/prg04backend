package com.fithub.fithub_api.avaliacao.controller;


import com.fithub.fithub_api.avaliacao.dto.AvaliacaoCreateDto;
import com.fithub.fithub_api.avaliacao.service.AvaliacaoIService;
import com.fithub.fithub_api.usuario.entity.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController {

    private final AvaliacaoIService avaliacaoService;

    @PostMapping("/treino/{treinoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> avaliarTreino(
            @PathVariable Long treinoId,
            @RequestBody @Valid AvaliacaoCreateDto dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        avaliacaoService.avaliarTreino(treinoId, dto, usuarioLogado);

        return ResponseEntity.ok().build();
    }
}
