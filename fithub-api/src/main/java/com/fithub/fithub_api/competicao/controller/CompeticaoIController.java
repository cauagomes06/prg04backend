package com.fithub.fithub_api.competicao.controller;

import com.fithub.fithub_api.competicao.dto.CompeticaoCreateDto;
import com.fithub.fithub_api.competicao.dto.CompeticaoResponseDto;
import com.fithub.fithub_api.inscricao.dto.InscricaoResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;

public interface CompeticaoIController {

    ResponseEntity<CompeticaoResponseDto> criarCompeticao(CompeticaoCreateDto createDto);

    ResponseEntity<List<CompeticaoResponseDto>> listarCompeticao();

    ResponseEntity<CompeticaoResponseDto> buscarPorId(Long id);

    ResponseEntity<Void> deletarCompeticao(Long id);

    ResponseEntity<CompeticaoResponseDto> editarCompeticao(CompeticaoCreateDto updateDto,Long id);

    ResponseEntity<List<InscricaoResponseDto>> getRankingDaCompeticao(Long id);

    public ResponseEntity<Void> atualizarStatus(Long id, String novoStatus);

    ResponseEntity<InscricaoResponseDto> inscrever(Long id, UserDetails userDetails);
}
