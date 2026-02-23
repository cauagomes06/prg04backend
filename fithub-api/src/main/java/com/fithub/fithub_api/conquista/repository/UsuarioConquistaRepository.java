package com.fithub.fithub_api.conquista.repository;

import com.fithub.fithub_api.conquista.entity.UsuarioConquista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioConquistaRepository extends JpaRepository<UsuarioConquista, Long> {

    // Verifica se o usuário já tem a medalha
    boolean existsByUsuarioIdAndConquistaId(Long usuarioId, Long conquistaId);

    // Traz a galeria de troféus já com os dados da conquista (evita N+1 queries)
    @Query("SELECT uc FROM UsuarioConquista uc JOIN FETCH uc.conquista WHERE uc.usuario.id = :usuarioId ORDER BY uc.dataDesbloqueio DESC")
    List<UsuarioConquista> findAllByUsuarioIdWithConquista(@Param("usuarioId") Long usuarioId);
}