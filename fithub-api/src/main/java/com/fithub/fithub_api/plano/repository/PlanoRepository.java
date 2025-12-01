package com.fithub.fithub_api.plano.repository;

import com.fithub.fithub_api.plano.entity.Plano;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanoRepository  extends JpaRepository<Plano, Long> {
    
    boolean existsByNome(@NotBlank(message = "O nome não pode ser vazio.") String nome);
}
