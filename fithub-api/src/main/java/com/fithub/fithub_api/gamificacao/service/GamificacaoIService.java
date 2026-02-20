package com.fithub.fithub_api.gamificacao.service;

import com.fithub.fithub_api.gamificacao.dto.UsuarioProgressoResponseDto;
import com.fithub.fithub_api.usuario.entity.Usuario;

public interface GamificacaoIService {

    String definirTitulo(int nivel);

    UsuarioProgressoResponseDto calcularProgresso(int scoreTotal);

     void adicionarPontos(Usuario usuario, int pontosGanhos);

    }
