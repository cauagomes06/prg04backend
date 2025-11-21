package com.fithub.fithub_api.treino.controller;

import com.fithub.fithub_api.treino.dto.TreinoResponseDto;
import com.fithub.fithub_api.treino.entity.Treino;
import com.fithub.fithub_api.treino.service.TreinoService;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.service.UsuarioService;
import com.fithub.fithub_api.treino.dto.TreinoCreateDto;
import com.fithub.fithub_api.treino.mapper.TreinoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treinos")
@RequiredArgsConstructor
public class TreinoController implements TreinoIController{

    private final TreinoService treinoService;
    private final UsuarioService usuarioService; // Para buscar a entidade Usuario

    @Override
    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TreinoResponseDto> criar(
            @Valid @RequestBody TreinoCreateDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuarioLogado = getUsuarioLogado(userDetails);
        Treino treinoSalvo = treinoService.criarTreino(dto, usuarioLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(TreinoMapper.toDto(treinoSalvo));
    }
    @Override
    @PutMapping("update/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TreinoResponseDto> atualizar(
            @PathVariable Long id,@RequestBody @Valid TreinoCreateDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuarioLogado = getUsuarioLogado(userDetails);
        Treino treinoAtualizado = treinoService.editarTreino(dto,usuarioLogado,id);


         return ResponseEntity.status(HttpStatus.OK).body(TreinoMapper.toDto(treinoAtualizado));
    }
    @Override
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<TreinoResponseDto> buscarPorId(@PathVariable Long id){

        Treino treino =  treinoService.buscarTreinoPorId(id);
        return ResponseEntity.ok().body(TreinoMapper.toDto(treino));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuarioLogado = getUsuarioLogado(userDetails);
        treinoService.deletarTreino(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/buscar")
    @PreAuthorize("isAuthenticated()")
     public ResponseEntity<List<TreinoResponseDto>> buscarTodosTreinosPublicos(){

       List<Treino> treinos =  treinoService.buscarTodosTreinosPublicos()   ;
        return ResponseEntity.ok().body(TreinoMapper.toListDto(treinos));
     }
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<TreinoResponseDto>> buscarTodosTreinos(){

        List<Treino> treinos =  treinoService.buscarTodos()   ;
        return ResponseEntity.ok().body(TreinoMapper.toListDto(treinos));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'PERSONAL')")
    @PatchMapping("/{id}/publicar")
    public ResponseEntity<TreinoResponseDto> publicar(
                    @PathVariable Long id,
                    @AuthenticationPrincipal UserDetails userDetails) {

                Usuario usuarioLogado = getUsuarioLogado(userDetails);
                Treino treinoPublicado = treinoService.publicarTreino(id, usuarioLogado);
                return ResponseEntity.ok(TreinoMapper.toDto(treinoPublicado));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/clonar")
    public ResponseEntity<TreinoResponseDto> clonarTreino(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuarioLogado = usuarioService.buscarPorUsername(userDetails.getUsername());

        Treino novoTreino = treinoService.clonarTreino(id, usuarioLogado);

        return ResponseEntity.status(HttpStatus.CREATED).body(TreinoMapper.toDto(novoTreino));
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<TreinoResponseDto>> buscarTreinosUsuario(@PathVariable Long id){

        List<Treino> treinos =  treinoService.buscarPorUsuarioId(id);
        return ResponseEntity.ok().body(TreinoMapper.toListDto(treinos));
    }

        public Usuario getUsuarioLogado(UserDetails userDetails) {

        String username = userDetails.getUsername();
            return usuarioService.buscarPorUsername(userDetails.getUsername());
        }
}
