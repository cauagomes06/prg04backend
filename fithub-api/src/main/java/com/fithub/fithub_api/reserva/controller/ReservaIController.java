package com.fithub.fithub_api.reserva.controller;

import com.fithub.fithub_api.reserva.dto.ReservaResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface ReservaIController {

     ResponseEntity<List<ReservaResponseDto>> listaReservas(UserDetails userDetails);

     ResponseEntity<Void> cancelarReserva( Long id , UserDetails userDetails);
}
