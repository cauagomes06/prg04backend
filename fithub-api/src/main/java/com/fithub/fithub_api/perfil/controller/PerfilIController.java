package com.fithub.fithub_api.perfil.controller;


import com.fithub.fithub_api.perfil.dto.PerfilCreateDto;
import com.fithub.fithub_api.perfil.dto.PerfilResponseDto;
import com.fithub.fithub_api.perfil.dto.PerfilUpdateDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface PerfilIController {


    public ResponseEntity<PerfilResponseDto> registrarPerfil(@RequestBody PerfilCreateDto perfilCreateDto);

    public ResponseEntity<Void> deletePerfil(Long id);

    public ResponseEntity<List< PerfilResponseDto>> listarPerfil();

    public ResponseEntity<PerfilResponseDto> buscarPerfilByid(Long id);

    public ResponseEntity<PerfilResponseDto> editarPerfil(@PathVariable Long id, @RequestBody @Valid PerfilUpdateDto updateDto);
}

