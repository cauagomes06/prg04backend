package com.fithub.fithub_api.execucao.mapper;

import com.fithub.fithub_api.execucao.dto.execucao.ExecucaoCreateDto;
import com.fithub.fithub_api.execucao.dto.execucao.TreinoExecucaoResponseDto;
import com.fithub.fithub_api.execucao.dto.item.ItemExecucaoResponseDto;
import com.fithub.fithub_api.execucao.entity.ItemExecucao;
import com.fithub.fithub_api.execucao.entity.TreinoExecucao;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;
@Component
public class ExecucaoMapper {

    public TreinoExecucaoResponseDto toResponseDto(TreinoExecucao treinoExecucao) {
        if (treinoExecucao == null) {
            return null;
        }

        TreinoExecucaoResponseDto dto = new TreinoExecucaoResponseDto();
        dto.setId(treinoExecucao.getId());
        dto.setTreinoId(treinoExecucao.getTreino().getId());
        dto.setNomeTreino(treinoExecucao.getTreino().getNome());
        dto.setDataInicio(treinoExecucao.getDataInicio());
        dto.setDataFim(treinoExecucao.getDataFim());
        dto.setDuracaoSegundos(treinoExecucao.getDuracaoSegundos());
        dto.setPontosGanhos(treinoExecucao.getPontosGanhos());

        // O campo 'conquistas' no DTO será preenchido manualmente no Service,
        // mas garantimos que a lista comece vazia e não nula para evitar erros no React
        dto.setConquistas(new ArrayList<>());

        if (treinoExecucao.getItens() != null) {
            dto.setItens(treinoExecucao.getItens().stream()
                    .map(this::toItemResponseDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public ItemExecucaoResponseDto toItemResponseDto(ItemExecucao itemExecucao) {
        ItemExecucaoResponseDto dto = new ItemExecucaoResponseDto();
        dto.setId(itemExecucao.getId());
        dto.setExercicioId(itemExecucao.getExercicio().getId());
        dto.setNomeExercicio(itemExecucao.getExercicio().getNome());
        dto.setSeriesConcluidas(itemExecucao.getSeriesConcluidas());
        return dto;
    }

    public TreinoExecucao toEntity(ExecucaoCreateDto dto) {
        if (dto == null) {
            return null;
        }

        TreinoExecucao entity = new TreinoExecucao();
        entity.setDataInicio(dto.getDataInicio());
        entity.setDataFim(dto.getDataFim());
        return entity;
    }
}