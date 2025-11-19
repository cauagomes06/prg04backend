package com.fithub.fithub_api.treino.controller;

import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.treino.dto.TreinoCreateDto;
import com.fithub.fithub_api.treino.dto.TreinoResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


public interface TreinoIController {

    public ResponseEntity<TreinoResponseDto> criar(TreinoCreateDto dto,
                                                   UserDetails userDetails);

    public ResponseEntity<Void> deletar(Long id,
          UserDetails userDetails);

    public ResponseEntity<TreinoResponseDto> buscarPorId( Long id);


    public ResponseEntity<TreinoResponseDto> publicar(
            Long id, UserDetails userDetails);

    public ResponseEntity<List<TreinoResponseDto>> buscarTodosTreinos();

    public ResponseEntity<List<TreinoResponseDto>> buscarTodosTreinosPublicos();

    public Usuario getUsuarioLogado(UserDetails userDetails);

    public ResponseEntity<TreinoResponseDto> clonarTreino(Long id, UserDetails userDetails);

    public ResponseEntity<TreinoResponseDto> atualizar(
             Long id, TreinoCreateDto dto,UserDetails userDetails);
}
