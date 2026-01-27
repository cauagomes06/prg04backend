package com.fithub.fithub_api.competicao.repository;

import com.fithub.fithub_api.competicao.entity.Competicao;
import com.fithub.fithub_api.competicao.entity.StatusCompeticao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CompeticaoRepository extends JpaRepository<Competicao, Long> {
    List<Competicao> findAllByStatusAndDataFimBefore(StatusCompeticao status, LocalDateTime dataAtual);
    List<Competicao> findByStatusInAndDataFimBefore(List<StatusCompeticao> status, LocalDateTime dataFim);

    // Isso evita buscar o usuário criador um por um na lista
    @Query(value = "SELECT c FROM Competicao c", // Adicione "LEFT JOIN FETCH c.criador" se tiver relacionamento
            countQuery = "SELECT count(c) FROM Competicao c")
    Page<Competicao> findAll(Pageable pageable);
}