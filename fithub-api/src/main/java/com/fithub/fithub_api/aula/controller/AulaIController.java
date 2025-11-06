package com.fithub.fithub_api.aula.controller;

import com.fithub.fithub_api.aula.dto.AulaCreateDto;
import com.fithub.fithub_api.aula.dto.AulaResponseDto;
import com.fithub.fithub_api.reserva.dto.ReservaResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.List;

public interface AulaIController {

    ResponseEntity<AulaResponseDto> createAula(AulaCreateDto aulaCreateDto);

    ResponseEntity<AulaResponseDto> buscarAulaPorId( Long id);

    ResponseEntity<AulaResponseDto> deleteAula( Long id);

    ResponseEntity<List<AulaResponseDto>> buscarAulas(Integer ano,Integer mes,Long instrutorId);

     ResponseEntity<ReservaResponseDto> reservarAula( Long id, UserDetails userDetails);
}
