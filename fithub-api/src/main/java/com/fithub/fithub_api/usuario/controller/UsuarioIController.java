package com.fithub.fithub_api.usuario.controller;

import com.fithub.fithub_api.web.dto.UsuarioCreateDto;
import com.fithub.fithub_api.web.dto.UsuarioResponseDto;
import com.fithub.fithub_api.web.dto.UsuarioSenhaDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface UsuarioIController {

    public ResponseEntity<UsuarioResponseDto> registrarUsuario(@Valid @RequestBody UsuarioCreateDto createDto);

    public ResponseEntity<Void> updateSenha(@PathVariable Long id , @Valid @RequestBody UsuarioSenhaDto senhaDto);

    public ResponseEntity<UsuarioResponseDto> buscarPorId(@PathVariable long id);

    public ResponseEntity<UsuarioResponseDto> deleteUsuario(@PathVariable long id);
}
