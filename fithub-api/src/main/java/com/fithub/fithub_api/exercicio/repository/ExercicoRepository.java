package com.fithub.fithub_api.exercicio.repository;

import com.fithub.fithub_api.exercicio.entity.Exercicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExercicoRepository  extends JpaRepository<Exercicio,Long> {

    public Exercicio findByNome(String nome);

    public Exercicio findById(long id);

    public List<Exercicio> findByGrupoMuscularContainingIgnoreCase(String grupoMuscular);

}
