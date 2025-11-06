package com.fithub.fithub_api.competicao.service;

import com.fithub.fithub_api.competicao.entity.Competicao;

import java.util.List;

public interface CompeticaoIService {

    Competicao create(Competicao competicao);

    List<Competicao> listarCompeticao();

    Competicao buscarPorId(Long id);

    void deletarCompeticao(Long id);

    Competicao editarCompeticao(Competicao competicao,Long id);
}
