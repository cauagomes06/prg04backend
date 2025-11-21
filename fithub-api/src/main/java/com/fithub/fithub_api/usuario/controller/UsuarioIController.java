package com.fithub.fithub_api.usuario.controller;

import com.fithub.fithub_api.pessoa.dto.PessoaUpdateDto;
import com.fithub.fithub_api.usuario.dto.UsuarioCreateDto;
import com.fithub.fithub_api.usuario.dto.UsuarioRankingDto;
import com.fithub.fithub_api.usuario.dto.UsuarioResponseDto;
import com.fithub.fithub_api.usuario.dto.UsuarioSenhaDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface UsuarioIController {

    public ResponseEntity<UsuarioResponseDto> registrarUsuario(UsuarioCreateDto createDto);

    public ResponseEntity<Void> updateSenha(Long id,UsuarioSenhaDto senhaDto, UserDetails userDetails);

    public ResponseEntity<List<UsuarioResponseDto>> buscaTodos();

    public ResponseEntity<UsuarioResponseDto> buscarPorId( Long id);

    public ResponseEntity<UsuarioResponseDto> deleteUsuario( Long id);

    public ResponseEntity<List<UsuarioRankingDto>> getRankingGeral();

    public  ResponseEntity<UsuarioResponseDto> getUsuarioLogado( UserDetails userDetails);

    public ResponseEntity<Void> atualizarPerfil( Long id, Long novoPerfilId);

    public ResponseEntity<Void> atualizarFotoPerfil(Map<String, String> payload, UserDetails userDetails);

    public ResponseEntity<UsuarioResponseDto> atualizarDadosPessoais( PessoaUpdateDto dto,UserDetails userDetails);
}
