package com.fithub.fithub_api.usuario.repository;

import com.fithub.fithub_api.perfil.entity.Perfil;
import com.fithub.fithub_api.plano.entity.Plano;
import com.fithub.fithub_api.usuario.entity.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestEntityManager entityManager; // Recomendado para testes JPA

    @Test
    @DisplayName("Deve retornar top utilizadores ordenados por score decrescente")
    void deveRetornarRankingOrdenado() {
        // Cenário: Criando usuários usando o método auxiliar refatorado
        criarUserComScore("top1", 1000);
        criarUserComScore("top3", 500);
        criarUserComScore("top2", 800);

        // Ação
        List<Usuario> ranking = usuarioRepository.findTop20ByOrderByScoreTotalDesc();

        // Verificação
        assertFalse(ranking.isEmpty());

        // Valida ordem: 1º lugar (1000 pts)
        assertEquals(1000, ranking.get(0).getScoreTotal());
        assertEquals("top1", ranking.get(0).getUsername());

        // Valida ordem: 2º lugar (800 pts)
        assertEquals(800, ranking.get(1).getScoreTotal());
        assertEquals("top2", ranking.get(1).getUsername());
    }

    private void criarUserComScore(String username, int score) {
        // Busca as referências do banco para evitar erro de "Transient Object"
        // Se preferir não usar o entityManager, pode instanciar manualmente como fazia antes,
        // mas passar dentro do builder é mais elegante.
        Perfil perfilCliente = entityManager.find(Perfil.class, 3L);
        Plano planoFit = entityManager.find(Plano.class, 1L);

        Usuario usuario = Usuario.builder()
                .username(username)
                .password("123")
                .scoreTotal(score)
                .perfil(perfilCliente) // Passa o objeto recuperado
                .plano(planoFit)       // Passa o objeto recuperado
                .build();

        usuarioRepository.save(usuario);
    }
}