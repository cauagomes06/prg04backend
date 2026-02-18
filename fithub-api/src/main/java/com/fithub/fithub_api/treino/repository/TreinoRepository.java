package com.fithub.fithub_api.treino.repository;

import com.fithub.fithub_api.treino.entity.Treino;
import com.fithub.fithub_api.treino.entity.StatusTreino;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreinoRepository extends JpaRepository<Treino, Long> {

    @Query("""
       SELECT t FROM Treino t
       JOIN t.criador u
       JOIN u.pessoa p
       WHERE t.status = :status
       AND (
            LOWER(t.nome) LIKE LOWER(CONCAT('%', :termo, '%'))
            OR LOWER(p.nomeCompleto) LIKE LOWER(CONCAT('%', :termo, '%'))
       )
       """)
    Page<Treino> buscarPorTermo(
            @Param("termo") String termo,
            @Param("status") StatusTreino status,
            Pageable pageable
    );

    @Query("""
           SELECT t FROM Treino t
           JOIN t.alunosSeguidores u
           WHERE t.status = com.fithub.fithub_api.treino.entity.StatusTreino.PUBLICO
           AND u.id = :usuarioId
           """)
    Page<Treino> buscarTreinosDeQuemSigo(@Param("usuarioId") Long usuarioId, Pageable pageable);

    @Query("""
           SELECT t FROM Treino t
           WHERE t.status = com.fithub.fithub_api.treino.entity.StatusTreino.PUBLICO
           ORDER BY t.id DESC
           """)
    Page<Treino> buscarRecentes(Pageable pageable);

    @Query("""
           SELECT t FROM Treino t
           WHERE t.status = com.fithub.fithub_api.treino.entity.StatusTreino.PUBLICO
           ORDER BY t.mediaNota DESC
           """)
    Page<Treino> buscarMelhoresAvaliados(Pageable pageable);

    @Query("""
           SELECT t FROM Treino t
           WHERE t.status = com.fithub.fithub_api.treino.entity.StatusTreino.PUBLICO
           ORDER BY SIZE(t.alunosSeguidores) DESC
           """)
    Page<Treino> buscarMaisSeguidos(Pageable pageable);

    List<Treino> findByCriadorId(Long criadorId);

    @Query("""
           SELECT t FROM Treino t
           WHERE t.status = com.fithub.fithub_api.treino.entity.StatusTreino.PUBLICO
           """)
    Page<Treino> findPublicos(Pageable pageable);
}