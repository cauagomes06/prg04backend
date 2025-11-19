package com.fithub.fithub_api.plano.service;

import com.fithub.fithub_api.plano.entity.Plano;

import com.fithub.fithub_api.plano.dto.PlanoResponseDto;
import com.fithub.fithub_api.plano.dto.PlanoUpdateDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface  PlanoIService {

    public Plano registrarPlano(@RequestBody Plano plano);

    List<Plano> buscarPlanos();

    Plano buscarPlanoById(Long id);

    void deletePlano(Long id);

    void mudarPlanoDoUsuario(Long usuarioId, Long novoPlanoId);

   PlanoResponseDto editarPlano(@PathVariable Long id, @RequestBody PlanoUpdateDto updateDto);
}
