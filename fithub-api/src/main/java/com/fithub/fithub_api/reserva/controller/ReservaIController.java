package com.fithub.fithub_api.reserva.controller;

import com.fithub.fithub_api.reserva.dto.ReservaCreateDto;
import com.fithub.fithub_api.reserva.dto.ReservaResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ReservaIController {

     ResponseEntity<List<ReservaResponseDto>> listaReservas(UserDetails userDetails);

     ResponseEntity<Void> cancelarReserva( Long id , UserDetails userDetails);
    public ResponseEntity<ReservaResponseDto> criarReserva( ReservaCreateDto dto,UserDetails userDetails);
}
