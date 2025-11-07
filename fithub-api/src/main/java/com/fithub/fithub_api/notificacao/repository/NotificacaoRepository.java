package com.fithub.fithub_api.notificacao.repository;

import com.fithub.fithub_api.notificacao.entity.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao,Long> {

    List<Notificacao> findByDestinatarioIdOrderByDataCriacaoDesc(Long destinatarioId);

    int countByDestinatarioIdAndLidaFalse(Long destinatarioId);
}
