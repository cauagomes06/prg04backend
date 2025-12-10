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

    // Entidade auxiliar para o perfil (pode ser mockada ou construída)
    private final Perfil perfilCliente = Perfil.builder().nome("ROLE_CLIENTE").build();

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao tentar apagar treino de outro utilizador")
    void naoDeveApagarTreinoDeOutro() {
        // 1. Preparar Dados (Mocks) usando o Builder

        // Dono do treino (ID 1)
        Usuario dono = Usuario.builder()
                .id(1L)
                .perfil(perfilCliente)
                .build();

        // Usuário invasor (ID 2)
        Usuario invasor = Usuario.builder()
                .id(2L)
                .perfil(perfilCliente)
                .build();

        // Treino criado pelo Dono
        Treino treino = Treino.builder()
                .id(10L)
                .criador(dono)
                .build();

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
        // Usuário que cria o treino
        Usuario usuario = Usuario.builder()
                .id(1L)
                .build();

        // DTO de criação de Treino
        TreinoCreateDto dto = TreinoCreateDto.builder()
                .nome("Treino A")
                .descricao("Hipertrofia")
                .items(new ArrayList<>()) // Sem itens para simplificar
                .build();

        // Mock do repositório: Retorna o mesmo objeto que foi passado
        when(treinoRepository.save(any(Treino.class))).thenAnswer(i -> i.getArguments()[0]);

        // Ação
        Treino resultado = treinoService.criarTreino(dto, usuario);

        // Verificação
        assertNotNull(resultado);
        assertEquals("Treino A", resultado.getNome());
        assertEquals(StatusTreino.PRIVADO, resultado.getStatus());
        assertEquals(usuario, resultado.getCriador());

        // Garante que o save foi chamado
        verify(treinoRepository, times(1)).save(any(Treino.class));
    }
}