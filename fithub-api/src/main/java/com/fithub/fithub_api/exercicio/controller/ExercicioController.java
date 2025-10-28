package com.fithub.fithub_api.exercicio.controller;


import com.fithub.fithub_api.exercicio.entity.Exercicio;
import com.fithub.fithub_api.exercicio.service.ExercicioService;
import com.fithub.fithub_api.web.dto.ExercicioCreateDto;
import com.fithub.fithub_api.web.dto.ExercicioResponseDto;
import com.fithub.fithub_api.web.dto.mapper.ExercicioMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exercicios")
public class ExercicioController implements ExercicioIController {

    private final ExercicioService exercicioService;


    @PostMapping("/register")
    public ResponseEntity<ExercicioResponseDto> registrar(@Valid @RequestBody ExercicioCreateDto exercicioCreateDto) {
        Exercicio novoExercicio = exercicioService.
                criarExercicio(ExercicioMapper.toExercicio(exercicioCreateDto));

        return  ResponseEntity.status(HttpStatus.CREATED).body(ExercicioMapper.toExercicioDto(novoExercicio));
        }


        @PutMapping("/update/{id}")
        @Override
    public ResponseEntity<ExercicioResponseDto> editar(@PathVariable Long id,
                                                       @Valid @RequestBody ExercicioCreateDto exercicioCreateDto) { //reutilizei o createDto ao inves de criar um updateDto

            Exercicio exercicioEditado = exercicioService.editarExercicio(id, exercicioCreateDto);
            return ResponseEntity.status(HttpStatus.OK).body(ExercicioMapper.toExercicioDto(exercicioEditado));
    }

    @DeleteMapping("/delete/{id}")
    @Override
    public ResponseEntity<ExercicioResponseDto> excluir(@PathVariable Long id) {

            exercicioService.deletarExercicio(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ExercicioResponseDto>> buscarTodos() {

      List<Exercicio> exercicios = exercicioService.buscarTodos();

        return ResponseEntity.ok().body(ExercicioMapper.toExercicioListDto(exercicios));
    }

    @Override
    @GetMapping(value ="/buscar",params = "grupoMuscular")
    public ResponseEntity<List<ExercicioResponseDto>> buscarPorGrupoMuscular(
            @RequestParam ("grupoMuscular") String grupoMuscular) {

        List<Exercicio> exercicios = exercicioService.buscarPorGrupoMuscular(grupoMuscular);

        return ResponseEntity.ok().body(ExercicioMapper.toExercicioListDto(exercicios));
    }

}
