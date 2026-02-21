package com.fithub.fithub_api.execucao.repository;

import com.fithub.fithub_api.execucao.entity.TreinoExecucao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExecucaoRepository  extends JpaRepository<TreinoExecucao, Integer> {

    // Busca o histórico de execuções de um usuário específico
    Page<TreinoExecucao> findByUsuarioIdOrderByDataInicioDesc(Long usuarioId, Pageable pageable);

    Page<TreinoExecucao> findByUsuarioId(Long usuarioId, Pageable pageable);

    // Verifica se existe execução para o usuário entre o início e o fim do dia de hoje
    boolean existsByUsuarioIdAndDataInicioBetween(
            Long usuarioId,
            LocalDateTime start,
            LocalDateTime end
    );
}
