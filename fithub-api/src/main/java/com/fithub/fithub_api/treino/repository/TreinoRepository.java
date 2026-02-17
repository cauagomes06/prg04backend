package com.fithub.fithub_api.treino.repository;

import com.fithub.fithub_api.treino.entity.StatusTreino;
import com.fithub.fithub_api.treino.entity.Treino;
import com.fithub.fithub_api.usuario.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TreinoRepository extends JpaRepository<Treino,Long> {

    Page<Treino> findAllByStatus(StatusTreino status, Pageable pageable);

    List<Treino> findByNome(String nome);

    List<Treino> findByCriador(Usuario criador);

    List<Treino> findByStatus (StatusTreino status);

    // CORREÇÃO: Usando 't.itensTreino'
    @Query("SELECT t FROM Treino t " +
            "LEFT JOIN FETCH t.itensTreino i " +
            "LEFT JOIN FETCH i.exercicio e " +
            "WHERE t.criador.id = :id")
    List<Treino> findByCriador_Id(@Param("id") Long id);

    // CORREÇÃO: Usando 't.itensTreino'
    @Query("SELECT t FROM Treino t " +
            "LEFT JOIN FETCH t.itensTreino i " +
            "LEFT JOIN FETCH i.exercicio e " +
            "WHERE t.id = :id")

    Optional<Treino> findByIdWithItens(@Param("id") Long id);

    @Query("SELECT t FROM Treino t LEFT JOIN t.alunosSeguidores s " +
            "WHERE t.status = :status " +
            "GROUP BY t " +
            "ORDER BY COUNT(s) DESC")
    Page<Treino> findAllByStatusOrderByFollowersDesc(@Param("status") StatusTreino status, Pageable pageable);
}
