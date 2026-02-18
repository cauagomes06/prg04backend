package com.fithub.fithub_api.treino.service;

import com.fithub.fithub_api.treino.entity.Treino;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.treino.dto.TreinoCreateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TreinoIService {

    public Treino criarTreino(TreinoCreateDto dto, Usuario criador);

    public void deletarTreino(Long idTreino, Usuario usuarioLogado);

    public Treino publicarTreino(Long idTreino, Usuario usuarioLogado);

    public Treino buscarTreinoPorId(Long id);

    public Page<Treino> buscarTodosTreinosPublicos(Pageable pageable);

    public Treino clonarTreino(Long treinoId, Usuario usuarioLogado);

    public Treino editarTreino(TreinoCreateDto dto, Usuario usuarioLogado, Long id);

    List<Treino> buscarPorUsuarioId(Long id);

    Page<Treino> buscarTodos(Pageable pageable);

    void deixarDeSeguirTreino(Long treinoId, Usuario usuarioLogado);

    void seguirTreino(Long treinoId, Usuario usuarioLogado);

    Page<Treino> buscarTreinosPorFiltro(String filtro, String termo, Pageable pageable, Usuario usuarioLogado);

}
