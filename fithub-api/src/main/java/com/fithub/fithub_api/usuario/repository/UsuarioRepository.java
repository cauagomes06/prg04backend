package com.fithub.fithub_api.usuario.repository;

import com.fithub.fithub_api.usuario.entity.StatusPlano;
import com.fithub.fithub_api.usuario.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByPlanoId(Long idPlano);
    boolean existsByUsername(String username);

    @Query("SELECT u FROM Usuario u " +
            "LEFT JOIN FETCH u.pessoa " +
            "LEFT JOIN FETCH u.perfil " +
            "LEFT JOIN FETCH u.plano " +
            "WHERE u.username = :username")
    Optional<Usuario> findByUsername(@Param("username") String username);


    Page<Usuario> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    @Query(
            value = "SELECT u FROM Usuario u " +
                    "LEFT JOIN FETCH u.pessoa " +
                    "LEFT JOIN FETCH u.perfil " +
                    "WHERE (:perfil = '' OR u.perfil.nome = :perfil) " +
                    "AND (:search = '' OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')))",
            countQuery = "SELECT COUNT(u) FROM Usuario u " +
                    "WHERE (:perfil = '' OR u.perfil.nome = :perfil) " +
                    "AND (:search = '' OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')))"
    )
    Page<Usuario> findWithFilters(@Param("search") String search, @Param("perfil") String perfil, Pageable pageable);

    @Query(value = "SELECT u FROM Usuario u LEFT JOIN FETCH u.pessoa ORDER BY u.scoreTotal DESC",
            countQuery = "SELECT count(u) FROM Usuario u")
    Page<Usuario> findAllByOrderByScoreTotalDesc(Pageable pageable);

    List<Usuario> findAllByPerfilNome(String nomePerfil);
    List<Usuario> findByStatusPlanoAndDataVencimentoPlanoBefore(StatusPlano status, LocalDate dataReferencia);
}