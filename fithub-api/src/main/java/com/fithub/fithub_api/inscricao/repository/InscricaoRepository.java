package com.fithub.fithub_api.inscricao.repository;

import com.fithub.fithub_api.inscricao.entity.Inscricao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    // Verifica se um usuario já se inscreveu numa competição
    boolean existsByUsuarioIdAndCompeticaoId(Long usuarioId, Long competicaoId);

    // Busca todas as inscrições de um usuario
    List<Inscricao> findAllByUsuarioId(Long usuarioId);

    // Busca uma inscrição específica de um usuario numa competição
    Optional<Inscricao> findByUsuarioIdAndCompeticaoId(Long usuarioId, Long competicaoId);

    // Busca todas as inscrições de uma competição
    List<Inscricao> findByCompeticaoId(Long competicaoId);
}
