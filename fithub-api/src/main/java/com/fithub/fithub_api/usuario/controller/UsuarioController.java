package com.fithub.fithub_api.usuario.controller;


import com.fithub.fithub_api.pessoa.dto.PessoaUpdateDto;
import com.fithub.fithub_api.pessoa.service.PessoaService;
import com.fithub.fithub_api.usuario.dto.UsuarioRankingDto;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.service.UsuarioService;
import com.fithub.fithub_api.usuario.dto.UsuarioCreateDto;
import com.fithub.fithub_api.usuario.dto.UsuarioResponseDto;
import com.fithub.fithub_api.usuario.dto.UsuarioSenhaDto;
import com.fithub.fithub_api.usuario.mapper.UsuarioMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController implements UsuarioIController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final PessoaService pessoaService;



    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDto> registrarUsuario(@Valid @RequestBody UsuarioCreateDto createDto) {
        Usuario usuarioSalvo = usuarioService.registrarUsuario(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioMapper.toDto(usuarioSalvo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> buscarPorId(@PathVariable long id) {

        Usuario user = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuarioMapper.toDto(user));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<UsuarioResponseDto> deleteUsuario(@PathVariable long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/update/senha/{id}")
            public ResponseEntity<Void> updateSenha(@PathVariable Long id ,@Valid @RequestBody UsuarioSenhaDto senhaDto){
        usuarioService.editarSenha(id,senhaDto.getSenhaAtual(),senhaDto.getNovaSenha(),senhaDto.getConfirmaSenha());

        return ResponseEntity.noContent().build();

    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> buscaTodos(){

        List<Usuario> usuarios = usuarioService.buscarTodos();

        return  ResponseEntity.ok(usuarioMapper.toListDto(usuarios));
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<UsuarioRankingDto>> getRankingGeral() {
        List<UsuarioRankingDto> ranking = usuarioService.getRankingGeral();
        return ResponseEntity.ok(ranking);
    }
    @PutMapping("/me/dados-pessoais")
    public ResponseEntity<UsuarioResponseDto> atualizarDadosPessoais(
            @RequestBody @Valid PessoaUpdateDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        //   Busca o usuário logado
        Usuario usuarioLogado = usuarioService.buscarPorUsername(userDetails.getUsername());

        //  Atualiza a Pessoa vinculada a este usuário
        if (usuarioLogado.getPessoa() != null) {
            pessoaService.atualizar(usuarioLogado.getPessoa().getId(), dto);
        }

        //  Retorna o DTO atualizado
        return ResponseEntity.ok(usuarioMapper.toDto(usuarioLogado));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDto> getUsuarioLogado(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        Usuario usuario = usuarioService.buscarPorUsername(userDetails.getUsername());
        UsuarioResponseDto dto = usuarioMapper.toDto(usuario);

        return ResponseEntity.ok(dto);
    }
}
