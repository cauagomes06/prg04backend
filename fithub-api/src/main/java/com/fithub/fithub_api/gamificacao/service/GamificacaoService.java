package com.fithub.fithub_api.gamificacao.service;

import com.fithub.fithub_api.gamificacao.dto.UsuarioProgressoResponseDto;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GamificacaoService implements GamificacaoIService {

    private final UsuarioRepository usuarioRepository;
    private static final int XP_POR_NIVEL = 1000;

    @Override
    public UsuarioProgressoResponseDto calcularProgresso(int scoreTotal) {
        int nivel = (scoreTotal / XP_POR_NIVEL) + 1;
        int xpAtualNoNivel = scoreTotal % XP_POR_NIVEL;
        double percentual = (xpAtualNoNivel / (double) XP_POR_NIVEL) * 100;

        return UsuarioProgressoResponseDto.builder()
                .scoreTotal(scoreTotal)
                .nivel(nivel)
                .xpAtualNoNivel(xpAtualNoNivel)
                .xpParaProximoNivel(XP_POR_NIVEL)
                .percentualProgresso(percentual)
                .tituloNivel(definirTitulo(nivel))
                .build();
    }

    @Override
    public String definirTitulo(int nivel) {
        if (nivel <= 5) return "Iniciante";
        if (nivel <= 15) return "Intermediário";
        if (nivel <= 30) return "Avançado";
        return "Mestre do Fit Hub";
    }
    public void adicionarPontos(Usuario usuario, int pontosGanhos) {
        int scoreAtual = usuario.getScoreTotal();
        int novoScore = scoreAtual + pontosGanhos;


        usuario.setScoreTotal(novoScore);
        usuarioRepository.save(usuario);
    }
}
