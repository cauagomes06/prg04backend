package com.fithub.fithub_api.treino.service;

import com.fithub.fithub_api.treino.entity.Treino;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.treino.dto.TreinoCreateDto;

import java.util.List;

public interface TreinoIService {

    public Treino criarTreino(TreinoCreateDto dto, Usuario criador);

    public void deletarTreino(Long idTreino, Usuario usuarioLogado);

    public Treino publicarTreino(Long idTreino, Usuario usuarioLogado);

    public Treino buscarTreinoPorId(Long id);

    public List<Treino> buscarTodosTreinosPublicos();

    public Treino editarTreino(TreinoCreateDto dto, Usuario usuarioLogado, Long id);

    List<Treino> buscarPorUsuarioId(Long id);
}
