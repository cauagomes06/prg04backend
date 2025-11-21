package com.fithub.fithub_api.treino.service;

import com.fithub.fithub_api.exercicio.service.ExercicioService;
import com.fithub.fithub_api.perfil.entity.Perfil;
import com.fithub.fithub_api.treino.dto.TreinoCreateDto;
import com.fithub.fithub_api.treino.entity.StatusTreino;
import com.fithub.fithub_api.treino.entity.Treino;
import com.fithub.fithub_api.treino.repository.TreinoRepository;
import com.fithub.fithub_api.usuario.entity.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreinoServiceTest {

    @Mock
    private TreinoRepository treinoRepository;

    @Mock
    private ExercicioService exercicioService;

    @InjectMocks
    private TreinoService treinoService;

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao tentar apagar treino de outro utilizador")
    void naoDeveApagarTreinoDeOutro() {
        // 1. Preparar Dados (Mocks)
        Usuario dono = new Usuario();
        dono.setId(1L);
        Perfil perfilCliente = new Perfil();
        perfilCliente.setNome("ROLE_CLIENTE");
        dono.setPerfil(perfilCliente);

        Usuario invasor = new Usuario();
        invasor.setId(2L);
        invasor.setPerfil(perfilCliente);

        Treino treino = new Treino();
        treino.setId(10L);
        treino.setCriador(dono);

        // Quando o repository procurar pelo ID 10, retorna este treino
        when(treinoRepository.findById(10L)).thenReturn(Optional.of(treino));

        // 2. Executar e Verificar Erro
        assertThrows(AccessDeniedException.class, () -> {
            treinoService.deletarTreino(10L, invasor); // Invasor tenta apagar
        });

        // Garante que o método delete NUNCA foi chamado no banco
        verify(treinoRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve criar treino com sucesso")
    void deveCriarTreino() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        TreinoCreateDto dto = new TreinoCreateDto();
        dto.setNome("Treino A");
        dto.setDescricao("Hipertrofia");
        dto.setItems(new ArrayList<>()); // Sem itens para simplificar

        when(treinoRepository.save(any(Treino.class))).thenAnswer(i -> i.getArguments()[0]);

        Treino resultado = treinoService.criarTreino(dto, usuario);

        assertNotNull(resultado);
        assertEquals("Treino A", resultado.getNome());
        assertEquals(StatusTreino.PRIVADO, resultado.getStatus());
        assertEquals(usuario, resultado.getCriador());
    }
}