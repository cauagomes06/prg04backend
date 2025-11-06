package com.fithub.fithub_api.inscricao.controller;

import com.fithub.fithub_api.inscricao.dto.InscricaoResponseDto;
import com.fithub.fithub_api.inscricao.dto.ResultadoSubmitDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


public interface InscricaoIController   {

    ResponseEntity<Void> cancelarInscricao(Long idInscricao,UserDetails userDetails);

    ResponseEntity<InscricaoResponseDto> submeterResultado( Long idInscricao, ResultadoSubmitDto dto, UserDetails userDetails);
}
