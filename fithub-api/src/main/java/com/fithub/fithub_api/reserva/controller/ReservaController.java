package com.fithub.fithub_api.reserva.controller;

import com.fithub.fithub_api.reserva.dto.ReservaCreateDto;
import com.fithub.fithub_api.reserva.entity.Reserva;
import com.fithub.fithub_api.reserva.service.ReservaService;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.service.UsuarioService;
import com.fithub.fithub_api.reserva.dto.ReservaResponseDto;
import com.fithub.fithub_api.reserva.mapper.ReservaMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @GetMapping("/usuario")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReservaResponseDto>> listaReservas(
            @AuthenticationPrincipal  Usuario usuarioLogado) {

        List<Reserva> minhasReservas = reservaService.buscarMinhasReservas(usuarioLogado);

        return ResponseEntity.ok(ReservaMapper.toListDto(minhasReservas));
    }
    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservaResponseDto> criarReserva(
            @RequestBody @Valid ReservaCreateDto dto,
            @AuthenticationPrincipal  Usuario usuarioLogado) {

        // Chama o serviço passando o ID que veio no corpo da requisição
        Reserva novaReserva = reservaService.criarReserva(dto.getAulaId(), usuarioLogado);

        return ResponseEntity.status(HttpStatus.CREATED).body(ReservaMapper.toDto(novaReserva));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id ,
    @AuthenticationPrincipal  Usuario usuarioLogado){
        reservaService.cancelarReserva(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/checkin")
    @PreAuthorize("hasRole('PERSONAL')") // Apenas instrutores
    public ResponseEntity<Void> realizarCheckIn(
            @PathVariable Long id,
            @AuthenticationPrincipal  Usuario usuarioLogado) {

        reservaService.realizarCheckIn(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
