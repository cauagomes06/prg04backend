package com.fithub.fithub_api.exercicio.service;

import com.fithub.fithub_api.exercicio.entity.Exercicio;
import com.fithub.fithub_api.exercicio.dto.ExercicioCreateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExercicioIService {

    public Exercicio buscarPorId(Long id);

    Exercicio criarExercicio(Exercicio exercicio);

    public void deletarExercicio(Long id);

    public  Exercicio editarExercicio(Long id,ExercicioCreateDto updateDto);

    Page<Exercicio> buscarTodos(Pageable pageable);

    Page<Exercicio> buscarPorGrupoMuscular(String nomeMusculo,Pageable pageable);

    public Page<Exercicio> buscarPorNome(String nome, Pageable pageable) ;

    }
