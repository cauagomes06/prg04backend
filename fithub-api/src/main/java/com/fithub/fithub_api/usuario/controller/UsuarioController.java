package com.fithub.fithub_api.usuario.controller;


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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController implements UsuarioIController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;



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

}
