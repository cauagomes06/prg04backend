package com.fithub.fithub_api.usuario.repository;

import com.fithub.fithub_api.usuario.entity.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    List<Usuario> findTop20ByOrderByScoreTotalDesc();

    List<Usuario> findAllByPerfilNome(String nomePerfil);
}
