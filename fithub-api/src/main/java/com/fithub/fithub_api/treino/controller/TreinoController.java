package com.fithub.fithub_api.treino.controller;

import com.fithub.fithub_api.treino.entity.Treino;
import com.fithub.fithub_api.treino.service.TreinoService;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.service.UsuarioService;
import com.fithub.fithub_api.treino.dto.TreinoCreateDto;
import com.fithub.fithub_api.web.dto.TreinoResponseDto;
import com.fithub.fithub_api.treino.mapper.TreinoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/register")
    public ResponseEntity<TreinoResponseDto> criar(
            @Valid @RequestBody TreinoCreateDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuarioLogado = getUsuarioLogado(userDetails);
        Treino treinoSalvo = treinoService.criarTreino(dto, usuarioLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(TreinoMapper.toDto(treinoSalvo));
    }
    @PutMapping("update/{id}")
    public ResponseEntity<TreinoResponseDto> atualizar(
            @PathVariable Long id,@RequestBody @Valid TreinoCreateDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuarioLogado = getUsuarioLogado(userDetails);
        Treino treinoAtualizado = treinoService.editarTreino(dto,usuarioLogado,id);


         return ResponseEntity.status(HttpStatus.OK).body(TreinoMapper.toDto(treinoAtualizado));
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
     public ResponseEntity<List<TreinoResponseDto>> buscarTodos(){

       List<Treino> treinos =  treinoService.buscarTodos();
        return ResponseEntity.ok().body(TreinoMapper.toListDto(treinos));
     }

        @PatchMapping("/publicar/{id}")
        public ResponseEntity<TreinoResponseDto> publicar(
                @PathVariable Long id,
                @AuthenticationPrincipal UserDetails userDetails) {

            Usuario usuarioLogado = getUsuarioLogado(userDetails);
            Treino treinoPublicado = treinoService.publicarTreino(id, usuarioLogado);
            return ResponseEntity.ok(TreinoMapper.toDto(treinoPublicado));
        }


        public Usuario getUsuarioLogado(UserDetails userDetails) {

        String username = userDetails.getUsername();
            return usuarioService.buscarPorUsername(userDetails.getUsername());
        }
}
