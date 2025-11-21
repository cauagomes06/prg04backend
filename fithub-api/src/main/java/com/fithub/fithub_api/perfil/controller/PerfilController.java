package com.fithub.fithub_api.perfil.controller;


import com.fithub.fithub_api.perfil.entity.Perfil;

import com.fithub.fithub_api.perfil.service.PerfilService;
import com.fithub.fithub_api.perfil.dto.PerfilCreateDto;
import com.fithub.fithub_api.perfil.dto.PerfilResponseDto;
import com.fithub.fithub_api.perfil.dto.PerfilUpdateDto;
import com.fithub.fithub_api.perfil.mapper.PerfilMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/perfil")
public class PerfilController implements PerfilIController {

    private final PerfilService perfilService;

    @Override
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PerfilResponseDto> registrarPerfil(PerfilCreateDto perfilCreateDto) {
        Perfil perfil = perfilService.registrarPerfil(PerfilMapper.toPerfil(perfilCreateDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(PerfilMapper.toPerfilDto(perfil));
    }

    @Override
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePerfil( @PathVariable Long id) {
        perfilService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PerfilResponseDto>> listarPerfil() {
        List<Perfil> perfilList = perfilService.buscarPerfis();
        return ResponseEntity.ok(PerfilMapper.toPerfilDtoList(perfilList));
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PerfilResponseDto> buscarPerfilByid( @PathVariable Long id) {

        Perfil perfil = perfilService.buscarPerfilByid(id);
        return ResponseEntity.ok(PerfilMapper.toPerfilDto(perfil));
    }

    @Override
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PerfilResponseDto> editarPerfil(
            @PathVariable Long id, @RequestBody @Valid PerfilUpdateDto updateDto) {

        PerfilResponseDto perfilAtualizado = perfilService.editarPerfil(id,updateDto);

        return ResponseEntity.ok(perfilAtualizado);
    }
}
