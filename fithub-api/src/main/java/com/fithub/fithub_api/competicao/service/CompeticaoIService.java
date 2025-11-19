package com.fithub.fithub_api.competicao.service;

import com.fithub.fithub_api.competicao.entity.Competicao;

import java.util.List;

public interface CompeticaoIService {

    Competicao create(Competicao competicao);

    List<Competicao> listarCompeticao();

    Competicao buscarPorId(Long id);

    void deletar(Long id);

    Competicao editarCompeticao(Competicao competicao,Long id);

    void atualizarStatus(Long id, String novoStatus);
}
