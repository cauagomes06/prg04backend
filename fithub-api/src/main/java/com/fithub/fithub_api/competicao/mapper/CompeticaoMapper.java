package com.fithub.fithub_api.competicao.mapper;

import com.fithub.fithub_api.competicao.dto.CompeticaoCreateDto;
import com.fithub.fithub_api.competicao.dto.CompeticaoResponseDto;
import com.fithub.fithub_api.competicao.entity.Competicao;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class CompeticaoMapper {



    public static Competicao toCompeticao(CompeticaoCreateDto createDto) {

        return new ModelMapper().map(createDto,Competicao.class);
    }

    public static CompeticaoResponseDto toDto(Competicao competicao) {
        return new ModelMapper().map(competicao,CompeticaoResponseDto.class);
    }

    public static List<CompeticaoResponseDto> toListDto(List<Competicao> competicaoList) {
        return competicaoList.stream().map(CompeticaoMapper:: toDto).collect(Collectors.toList());
    }
}
