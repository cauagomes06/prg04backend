package com.fithub.fithub_api.exercicio.controller;



import com.fithub.fithub_api.exercicio.dto.ExercicioCreateDto;
import com.fithub.fithub_api.exercicio.dto.ExercicioResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ExercicioIController {

    public ResponseEntity<ExercicioResponseDto> registrar(ExercicioCreateDto exercicioCreateDto);

    public ResponseEntity<ExercicioResponseDto> editar(Long id,ExercicioCreateDto exercicioCreateDto);

    public ResponseEntity<ExercicioResponseDto> excluir(Long id);

    public ResponseEntity<List<ExercicioResponseDto>> buscarTodos();

    public ResponseEntity<List<ExercicioResponseDto>> buscarPorGrupoMuscular(String nomeMusculo);
}
