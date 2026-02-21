package com.fithub.fithub_api.execucao.service;

import com.fithub.fithub_api.execucao.dto.execucao.ExecucaoCreateDto;
import com.fithub.fithub_api.execucao.dto.execucao.TreinoExecucaoResponseDto;
import com.fithub.fithub_api.usuario.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExecucaoIService {

    TreinoExecucaoResponseDto registrarExecucao(ExecucaoCreateDto dto);
    Page<TreinoExecucaoResponseDto> buscarHistoricoPaginado(Long usuarioId, Pageable pageable);

    boolean jaTreinouHoje();
}
