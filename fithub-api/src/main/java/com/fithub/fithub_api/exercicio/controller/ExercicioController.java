package com.fithub.fithub_api.exercicio.controller;


import com.fithub.fithub_api.exercicio.dto.ExercicioCreateDto;
import com.fithub.fithub_api.exercicio.dto.ExercicioResponseDto;
import com.fithub.fithub_api.exercicio.entity.Exercicio;
import com.fithub.fithub_api.exercicio.mapper.ExercicioMapper;
import com.fithub.fithub_api.exercicio.service.ExercicioService;



import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exercicios")
public class ExercicioController  {

    private final ExercicioService exercicioService;



    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'PERSONAL')")
    public ResponseEntity<ExercicioResponseDto> registrar(@Valid @RequestBody ExercicioCreateDto exercicioCreateDto) {
        Exercicio novoExercicio = exercicioService.
                criarExercicio(ExercicioMapper.toExercicio(exercicioCreateDto));

        return  ResponseEntity.status(HttpStatus.CREATED).body(ExercicioMapper.toExercicioDto(novoExercicio));
        }


        @PutMapping("/update/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'PERSONAL')")
    public ResponseEntity<ExercicioResponseDto> editar(@PathVariable Long id,
                                                       @Valid @RequestBody ExercicioCreateDto exercicioCreateDto) { //reutilizei o createDto ao inves de criar um updateDto

            Exercicio exercicioEditado = exercicioService.editarExercicio(id, exercicioCreateDto);
            return ResponseEntity.status(HttpStatus.OK).body(ExercicioMapper.toExercicioDto(exercicioEditado));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PERSONAL')")
    public ResponseEntity<ExercicioResponseDto> excluir(@PathVariable Long id) {

            exercicioService.deletarExercicio(id);

        return ResponseEntity.noContent().build();
    }
    @GetMapping("/buscar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ExercicioResponseDto>> buscarTodos(Pageable pageable) {

      Page<Exercicio> exercicios = exercicioService.buscarTodos(pageable);

      Page<ExercicioResponseDto> pageExercicioDto = exercicios.map(ExercicioMapper::toExercicioDto);

        return ResponseEntity.ok().body(pageExercicioDto);
    }

    @GetMapping(value ="/buscar",params = "grupoMuscular")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ExercicioResponseDto>> buscarPorGrupoMuscular(
            @RequestParam ("grupoMuscular") String grupoMuscular) {

        List<Exercicio> exercicios = exercicioService.buscarPorGrupoMuscular(grupoMuscular);

        return ResponseEntity.ok().body(ExercicioMapper.toExercicioListDto(exercicios));
    }

}
