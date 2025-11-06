package com.fithub.fithub_api.inscricao.service;

import com.fithub.fithub_api.inscricao.dto.ResultadoSubmitDto;
import com.fithub.fithub_api.inscricao.entity.Inscricao;
import com.fithub.fithub_api.usuario.entity.Usuario;

import java.util.List;

public interface InscricaoIService {

     Inscricao inscreverEmCompeticao(Long idCompeticao, Usuario usuarioLogado);

     Inscricao enviarResultado(Long idInscricao, ResultadoSubmitDto dto, Usuario usuarioLogado);

     void cancelarInscricao(Long idInscricao, Usuario usuarioLogado);

     List<Inscricao> buscarInscricoesPorCompeticaoOrdenado(Long idCompeticao);
}
