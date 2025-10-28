package com.fithub.fithub_api.exercicio.service;

import com.fithub.fithub_api.exercicio.entity.Exercicio;
import com.fithub.fithub_api.web.dto.ExercicioCreateDto;

import java.util.List;
import java.util.Optional;

public interface ExercicioIService {

    public Exercicio buscarPorId(Long id);

    Exercicio criarExercicio(Exercicio exercicio);

    public void deletarExercicio(Long id);

    public  Exercicio editarExercicio(Long id,ExercicioCreateDto updateDto);

    List<Exercicio> buscarTodos();

    List<Exercicio> buscarPorGrupoMuscular(String nomeMusculo);
}
