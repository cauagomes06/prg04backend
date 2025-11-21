package com.fithub.fithub_api.usuario.repository;

import com.fithub.fithub_api.perfil.entity.Perfil;
import com.fithub.fithub_api.plano.entity.Plano;
import com.fithub.fithub_api.usuario.entity.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test") // Garante que usa o H2 e data.sql de teste
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve retornar top utilizadores ordenados por score decrescente")
    void deveRetornarRankingOrdenado() {
        // O data.sql já deve ter inserido Perfis e Planos (IDs 1, 2, 3...)
        // Vamos criar users manualmente para controlar o Score
        criarUserComScore("top1", 1000);
        criarUserComScore("top3", 500);
        criarUserComScore("top2", 800);

        // Ação
        List<Usuario> ranking = usuarioRepository.findTop20ByOrderByScoreTotalDesc();

        // Verificação
        assertEquals(3, ranking.size()); // Devem vir 3 (ou mais se o data.sql tiver users)

        // O primeiro deve ser o de 1000 pontos
        assertEquals(1000, ranking.get(0).getScoreTotal());
        assertEquals("top1", ranking.get(0).getUsername());

        // O segundo deve ser o de 800 pontos
        assertEquals(800, ranking.get(1).getScoreTotal());
        assertEquals("top2", ranking.get(1).getUsername());
    }

    private void criarUserComScore(String username, int score) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPassword("123");
        u.setScoreTotal(score);

        // Associa a entidades que já existem no data.sql (mock/dummy)
        Perfil p = new Perfil(); p.setId(3L); // Cliente
        u.setPerfil(p);

        Plano pl = new Plano(); pl.setId(1L); // Fit
        u.setPlano(pl);

        usuarioRepository.save(u);
    }
}