package com.fithub.fithub_api.pessoa.service;

import com.fithub.fithub_api.pessoa.dto.PessoaUpdateDto;
import com.fithub.fithub_api.pessoa.entity.Pessoa;

public interface PessoaIService {

    public Pessoa atualizar(Long id, PessoaUpdateDto dto);
}
