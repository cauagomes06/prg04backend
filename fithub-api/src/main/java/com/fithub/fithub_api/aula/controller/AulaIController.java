package com.fithub.fithub_api.aula.controller;

import com.fithub.fithub_api.aula.dto.AulaCreateDto;
import com.fithub.fithub_api.aula.dto.AulaResponseDto;
import com.fithub.fithub_api.aula.dto.InstrutorResponseDto;
import com.fithub.fithub_api.aula.dto.ParticipanteDto;
import com.fithub.fithub_api.reserva.dto.ReservaResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;

public interface AulaIController {

    ResponseEntity<AulaResponseDto> createAula(AulaCreateDto aulaCreateDto);

    ResponseEntity<AulaResponseDto> buscarAulaPorId( Long id);

    ResponseEntity<AulaResponseDto> deleteAula( Long id);

    ResponseEntity<List<AulaResponseDto>> buscarAulas(Integer ano,Integer mes,Long instrutorId);

    public ResponseEntity<List<ParticipanteDto>> getParticipantes( Long id,UserDetails userDetails);

    ResponseEntity<List<InstrutorResponseDto>> getInstrutores();
}
