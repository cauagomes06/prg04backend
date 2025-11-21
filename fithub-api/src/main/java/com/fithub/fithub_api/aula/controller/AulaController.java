package com.fithub.fithub_api.aula.controller;


import com.fithub.fithub_api.aula.dto.*;
import com.fithub.fithub_api.aula.entity.Aula;
import com.fithub.fithub_api.aula.mapper.AulaMapper;
import com.fithub.fithub_api.aula.service.AulaService;
import com.fithub.fithub_api.exception.EntityNotFoundException;
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
@RequiredArgsConstructor
@RequestMapping("/api/aulas")
public class AulaController implements AulaIController {

    private final UsuarioService usuarioService;
    private final AulaService aulaService;

    @PreAuthorize("hasAnyRole('ADMIN', 'PERSONAL')")
    @PostMapping("/register")
    public ResponseEntity<AulaResponseDto> createAula(@Valid @RequestBody AulaCreateDto aulaCreateDto){

      Aula novaAula = aulaService.create(aulaCreateDto);

      return ResponseEntity.status(HttpStatus.CREATED).body(AulaMapper.toAulaDto(novaAula));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'PERSONAL')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<AulaResponseDto> deleteAula(@PathVariable Long id){

            aulaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AulaResponseDto> buscarAulaPorId(@PathVariable Long id){

        Aula aulaEncontrada =aulaService.buscarPorId(id);

        return ResponseEntity.ok().body(AulaMapper.toAulaDto(aulaEncontrada));
    }

    @GetMapping("/buscar")
    @PreAuthorize("isAuthenticated()")
        public ResponseEntity<List<AulaResponseDto>> buscarAulas(
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "instrutorId", required = false) Long instrutorId
    ){

        List<Aula> aula = aulaService.buscarAulasComFiltro(ano, mes, instrutorId);

        return ResponseEntity.ok().body(AulaMapper.toAulaDtoList(aula));
        }

        @PatchMapping("/update/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'PERSONAL')")
        public ResponseEntity<AulaResponseDto> editarAula(
                @RequestBody AulaUpdateDto updateDto,
                @PathVariable Long id,
                @AuthenticationPrincipal UserDetails userDetails) {

            Usuario usuarioLogado = getUsuarioLogado(userDetails);
            Aula aulaEditada = aulaService.editarAula(id,updateDto,usuarioLogado);
            return ResponseEntity.ok().body(AulaMapper.toAulaDto(aulaEditada));
        }

    @GetMapping("/instrutores")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<InstrutorResponseDto>> getInstrutores() {
        List<InstrutorResponseDto> instrutores = usuarioService.buscarInstrutores();
        return ResponseEntity.ok(instrutores);
    }

    @GetMapping("/{id}/participantes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ParticipanteDto>> getParticipantes(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 1. Identifica quem está a fazer o pedido
        Usuario usuarioLogado = getUsuarioLogado(userDetails);
        List<ParticipanteDto> participantes = aulaService.buscarParticipantes(id, usuarioLogado);

        return ResponseEntity.ok(participantes);
    }


    private Usuario getUsuarioLogado(UserDetails userDetails) {
        if (userDetails == null) {
            throw new EntityNotFoundException("Usuário não autenticado.");
        }

        String username = userDetails.getUsername();

        // Usa o UsuarioService para buscar a sua entidade completa
        return usuarioService.buscarPorUsername(username);
    }

}
