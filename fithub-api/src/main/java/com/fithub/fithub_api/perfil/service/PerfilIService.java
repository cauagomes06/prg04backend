package com.fithub.fithub_api.perfil.service;

import com.fithub.fithub_api.perfil.entity.Perfil;
import com.fithub.fithub_api.web.dto.PerfilResponseDto;
import com.fithub.fithub_api.web.dto.PerfilUpdateDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface PerfilIService {
    Perfil registrarPerfil(@RequestBody Perfil perfil);

    List<Perfil> buscarPerfis();

    Perfil buscarPerfilByid(Long id);

    void delete(Long id);

    PerfilResponseDto editarPerfil(Long id, @Valid PerfilUpdateDto updateDto);
}
