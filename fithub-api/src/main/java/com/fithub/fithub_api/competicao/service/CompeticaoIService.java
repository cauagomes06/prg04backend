package com.fithub.fithub_api.competicao.service;

import com.fithub.fithub_api.competicao.entity.Competicao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompeticaoIService {

    Competicao create(Competicao competicao);

    Page<Competicao> listarCompeticao(Pageable pageable);

    Competicao buscarPorId(Long id);

    void deletar(Long id);

    Competicao editarCompeticao(Competicao competicao, Long id);

    void atualizarStatus(Long id, String novoStatus);
}