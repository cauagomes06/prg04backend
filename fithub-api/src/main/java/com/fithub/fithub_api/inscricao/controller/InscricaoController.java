package com.fithub.fithub_api.inscricao.controller;


import com.fithub.fithub_api.inscricao.dto.InscricaoResponseDto;
import com.fithub.fithub_api.inscricao.dto.ResultadoSubmitDto;
import com.fithub.fithub_api.inscricao.entity.Inscricao;
import com.fithub.fithub_api.inscricao.mapper.InscricaoMapper;
import com.fithub.fithub_api.inscricao.service.InscricaoIService;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/competicoes/inscricao")
public class InscricaoController implements InscricaoIController{

    private final InscricaoIService inscricaoIService;
    private final UsuarioService usuarioIService;

    @PostMapping("/{idInscricao}/resultado")
    public ResponseEntity<InscricaoResponseDto> submeterResultado
            (@PathVariable Long idInscricao,
                 @Valid @RequestBody ResultadoSubmitDto dto,
             @AuthenticationPrincipal UserDetails userDetails){

        Usuario usuarioLogado = getUsuarioLogado(userDetails);

        Inscricao inscricaoAtualizada = inscricaoIService.enviarResultado(idInscricao,dto,usuarioLogado);

        return ResponseEntity.ok().body(InscricaoMapper.toDto(inscricaoAtualizada));
    }

    @DeleteMapping("/delete/{idInscricao}")
    public ResponseEntity<Void> cancelarInscricao(
            @PathVariable Long idInscricao,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuarioLogado = getUsuarioLogado(userDetails);
        inscricaoIService.cancelarInscricao(idInscricao, usuarioLogado);

        return ResponseEntity.noContent().build();
    }



    // --- Método Auxiliar ---
    private Usuario getUsuarioLogado(UserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("Usuario não autenticado.");
        }
        return usuarioIService.buscarPorUsername(userDetails.getUsername());
    }
}
