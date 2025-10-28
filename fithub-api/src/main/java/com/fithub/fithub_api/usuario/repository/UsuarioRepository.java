package com.fithub.fithub_api.usuario.repository;

import com.fithub.fithub_api.usuario.entity.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByPlanoId(Long idPlano);

    boolean existsByUsername(String username);

    Optional<Usuario> findByUsername(String username);
}
