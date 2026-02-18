package com.fithub.fithub_api.avaliacao.repository;

import com.fithub.fithub_api.avaliacao.entity.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AvaliacaoRepository  extends JpaRepository<Avaliacao, Long> {

    // Buscar a avaliação específica de um usuário em um treino (para  poder editar a nota )
    Optional<Avaliacao> findByTreinoIdAndUsuarioId(Long treinoId, Long usuarioId);

    // Calcular a média direto no banco
    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.treino.id = :treinoId")
    Double calcularMediaPorTreino(Long treinoId);

    // No AvaliacaoRepository.java
    long countByTreinoId(Long treinoId);
}
