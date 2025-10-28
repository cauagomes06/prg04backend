package com.fithub.fithub_api.exercicio.controller;

import com.fithub.fithub_api.web.dto.ExercicioCreateDto;
import com.fithub.fithub_api.web.dto.ExercicioResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ExercicioIController {

    public ResponseEntity<ExercicioResponseDto> registrar(ExercicioCreateDto exercicioCreateDto);

    public ResponseEntity<ExercicioResponseDto> editar(Long id,ExercicioCreateDto exercicioCreateDto);

    public ResponseEntity<ExercicioResponseDto> excluir(Long id);

    public ResponseEntity<List<ExercicioResponseDto>> buscarTodos();

    public ResponseEntity<List<ExercicioResponseDto>> buscarPorGrupoMuscular(String nomeMusculo);
}
