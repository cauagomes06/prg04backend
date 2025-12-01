package com.fithub.fithub_api.perfil.repository;

import com.fithub.fithub_api.perfil.entity.Perfil;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilRepository  extends JpaRepository<Perfil, Long> {
    boolean existsByNome(@NotBlank(message = "O nome não pode ser vazio.") String nome);
}
