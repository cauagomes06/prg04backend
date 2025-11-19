package com.fithub.fithub_api.competicao.repository;

import com.fithub.fithub_api.competicao.entity.Competicao;
import com.fithub.fithub_api.competicao.entity.StatusCompeticao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CompeticaoRepository extends JpaRepository<Competicao, Long> {

    List<Competicao> findAllByStatusAndDataFimBefore(StatusCompeticao status, LocalDateTime dataAtual);
    List<Competicao> findByStatusInAndDataFimBefore(List<StatusCompeticao> status, LocalDateTime dataFim);
}
