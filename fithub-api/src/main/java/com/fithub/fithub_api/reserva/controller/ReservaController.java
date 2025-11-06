package com.fithub.fithub_api.reserva.controller;

import com.fithub.fithub_api.reserva.entity.Reserva;
import com.fithub.fithub_api.reserva.service.ReservaService;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.service.UsuarioService;
import com.fithub.fithub_api.reserva.dto.ReservaResponseDto;
import com.fithub.fithub_api.reserva.mapper.ReservaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController implements ReservaIController{

    private final ReservaService reservaService;
    private final UsuarioService usuarioService;

    @GetMapping("/minhas")
    public ResponseEntity<List<ReservaResponseDto>> listaReservas(
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuarioLogado = getUsuarioLogado(userDetails);
        List<Reserva> minhasReservas = reservaService.buscarMinhasReservas(usuarioLogado);

        return ResponseEntity.ok(ReservaMapper.toListDto(minhasReservas));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id ,
    @AuthenticationPrincipal UserDetails userDetails){
        Usuario usuarioLogado = getUsuarioLogado(userDetails);
        reservaService.cancelarReserva(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
    // Método auxiliar
    private Usuario getUsuarioLogado(UserDetails userDetails) {
        return usuarioService.buscarPorUsername(userDetails.getUsername());
    }
}
