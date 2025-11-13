package com.fithub.fithub_api.treino.repository;

import com.fithub.fithub_api.treino.entity.StatusTreino;
import com.fithub.fithub_api.treino.entity.Treino;
import com.fithub.fithub_api.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TreinoRepository extends JpaRepository<Treino,Long> {

    List<Treino> findAllByStatus(StatusTreino status);

    List<Treino> findByNome(String nome);

    List<Treino> findByCriador(Usuario criador);

    List<Treino> findByStatus (StatusTreino status);

    List<Treino> findByCriador_Id(Long id);
}
