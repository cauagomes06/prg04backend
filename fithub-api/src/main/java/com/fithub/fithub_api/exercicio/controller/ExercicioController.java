package com.fithub.fithub_api.exercicio.controller;


import com.fithub.fithub_api.exercicio.dto.ExercicioCreateDto;
import com.fithub.fithub_api.exercicio.dto.ExercicioResponseDto;
import com.fithub.fithub_api.exercicio.entity.Exercicio;
import com.fithub.fithub_api.exercicio.mapper.ExercicioMapper;
import com.fithub.fithub_api.exercicio.service.ExercicioService;



import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public ResponseEntity<Page<ExercicioResponseDto>> buscarTodos(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            @RequestParam(value = "search", required = false) String search) { // Recebe o texto

        Pageable pageable = PageRequest.of(page, size);
        Page<Exercicio> exercicios;

        // LÓGICA DE FILTRO:
        if (search != null && !search.trim().isEmpty()) {
            // Se o usuário digitou algo, chama o método de busca específica no Service
            exercicios = exercicioService.buscarPorGrupoMuscular(search, pageable);
        } else {
            // Se a busca estiver vazia, traz todos (comportamento padrão)
            exercicios = exercicioService.buscarTodos(pageable);
        }

        Page<ExercicioResponseDto> pageExercicioDto = exercicios.map(ExercicioMapper::toExercicioDto);

        return ResponseEntity.ok().body(pageExercicioDto);
    }

    @GetMapping(value = "/buscar", params = "grupoMuscular")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ExercicioResponseDto>> buscarPorGrupoMuscular(
            @RequestParam("grupoMuscular") String grupoMuscular,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size) {

        // 1. Cria o objeto Pageable manualmente (igual fizemos no buscarTodos)
        Pageable pageable = PageRequest.of(page, size);

        // 2. Chama o serviço (que agora retorna Page<Exercicio>)
        Page<Exercicio> exerciciosPage = exercicioService.buscarPorGrupoMuscular(grupoMuscular, pageable);

        // 3. Converte Page<Entity> para Page<Dto>
        Page<ExercicioResponseDto> dtoPage = exerciciosPage.map(ExercicioMapper::toExercicioDto);

        return ResponseEntity.ok().body(dtoPage);
    }
}
