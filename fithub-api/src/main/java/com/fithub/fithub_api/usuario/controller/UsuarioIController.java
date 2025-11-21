package com.fithub.fithub_api.usuario.controller;

import com.fithub.fithub_api.usuario.dto.UsuarioCreateDto;
import com.fithub.fithub_api.usuario.dto.UsuarioRankingDto;
import com.fithub.fithub_api.usuario.dto.UsuarioResponseDto;
import com.fithub.fithub_api.usuario.dto.UsuarioSenhaDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface UsuarioIController {

    public ResponseEntity<UsuarioResponseDto> registrarUsuario(UsuarioCreateDto createDto);

    public ResponseEntity<Void> updateSenha( Long id , UsuarioSenhaDto senhaDto);

    public ResponseEntity<List<UsuarioResponseDto>> buscaTodos();

    public ResponseEntity<UsuarioResponseDto> buscarPorId( Long id);

    public ResponseEntity<UsuarioResponseDto> deleteUsuario( Long id);

    public ResponseEntity<List<UsuarioRankingDto>> getRankingGeral();

    public ResponseEntity<Void> atualizarPerfil( Long id, Long novoPerfilId);
}
