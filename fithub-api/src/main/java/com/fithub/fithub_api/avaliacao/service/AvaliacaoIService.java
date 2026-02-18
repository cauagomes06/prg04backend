package com.fithub.fithub_api.avaliacao.service;

import com.fithub.fithub_api.avaliacao.dto.AvaliacaoCreateDto;
import com.fithub.fithub_api.usuario.entity.Usuario;

public interface AvaliacaoIService {

   void avaliarTreino(Long treinoId, AvaliacaoCreateDto dto, Usuario usuario);
}
