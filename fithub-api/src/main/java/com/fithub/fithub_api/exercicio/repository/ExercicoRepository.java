package com.fithub.fithub_api.exercicio.repository;

import com.fithub.fithub_api.exercicio.entity.Exercicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExercicoRepository  extends JpaRepository<Exercicio,Long> {

    public Exercicio findByNome(String nome);

    public Exercicio findById(long id);

    Page<Exercicio> findByGrupoMuscularContainingIgnoreCase(String grupoMuscular, Pageable pageable);

    Page<Exercicio> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
