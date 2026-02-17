package com.fithub.fithub_api.treino.controller;

import com.fithub.fithub_api.treino.dto.TreinoResponseDto;
import com.fithub.fithub_api.treino.entity.Treino;
import com.fithub.fithub_api.treino.service.TreinoService;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.treino.dto.TreinoCreateDto;
import com.fithub.fithub_api.treino.mapper.TreinoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treinos")
@RequiredArgsConstructor
public class TreinoController {

    private final TreinoService treinoService;


    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TreinoResponseDto> criar(
            @Valid @RequestBody TreinoCreateDto dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Treino treinoSalvo = treinoService.criarTreino(dto, usuarioLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(TreinoMapper.toDto(treinoSalvo));
    }

    @PutMapping("update/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TreinoResponseDto> atualizar(
            @PathVariable Long id, @RequestBody @Valid TreinoCreateDto dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {


        Treino treinoAtualizado = treinoService.editarTreino(dto, usuarioLogado, id);


        return ResponseEntity.status(HttpStatus.OK).body(TreinoMapper.toDto(treinoAtualizado));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<TreinoResponseDto> buscarPorId(@PathVariable Long id) {

        Treino treino = treinoService.buscarTreinoPorId(id);
        return ResponseEntity.ok().body(TreinoMapper.toDto(treino));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {


        treinoService.deletarTreino(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TreinoResponseDto>> buscarTodosTreinosPublicos(
            Pageable pageable,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Page<Treino> treinos = treinoService.buscarTodosTreinosPublicos(pageable);

        Page<TreinoResponseDto> responseDtoPage = treinos
                .map(treino -> TreinoMapper.toDto(treino, usuarioLogado));

        return ResponseEntity.ok().body(responseDtoPage);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Page<TreinoResponseDto>> buscarTodosTreinos(Pageable pageable) {

        Page<Treino> treinos = treinoService.buscarTodos(pageable);

        Page<TreinoResponseDto> pageableDto = treinos.map(TreinoMapper::toDto);
        return ResponseEntity.ok().body(pageableDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PERSONAL')")
    @PatchMapping("/{id}/publicar")
    public ResponseEntity<TreinoResponseDto> publicar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {


        Treino treinoPublicado = treinoService.publicarTreino(id, usuarioLogado);
        return ResponseEntity.ok(TreinoMapper.toDto(treinoPublicado));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/clonar")
    public ResponseEntity<TreinoResponseDto> clonarTreino(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {


        Treino novoTreino = treinoService.clonarTreino(id, usuarioLogado);

        return ResponseEntity.status(HttpStatus.CREATED).body(TreinoMapper.toDto(novoTreino));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<TreinoResponseDto>> buscarTreinosUsuario(@PathVariable Long id) {

        List<Treino> treinos = treinoService.buscarPorUsuarioId(id);
        return ResponseEntity.ok().body(TreinoMapper.toListDto(treinos));
    }

    @PostMapping("/{id}/seguir")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> seguirTreino(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        treinoService.seguirTreino(id, usuarioLogado);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/deixar-de-seguir")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deixarDeSeguirTreino(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        treinoService.deixarDeSeguirTreino(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}

