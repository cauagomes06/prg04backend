package com.fithub.fithub_api.web.dto.mapper;

import com.fithub.fithub_api.plano.entity.Plano;
import com.fithub.fithub_api.web.dto.PlanoCreateDto;
import com.fithub.fithub_api.web.dto.PlanoResponseDto;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class PlanoMapper {

    public static Plano toPlano(PlanoCreateDto planoCreateDto) {
        return new ModelMapper().map(planoCreateDto, Plano.class);
    }

    public static PlanoResponseDto toPlanoDto(Plano plano) {
        return new ModelMapper().map(plano, PlanoResponseDto.class);
    }
    public static List<PlanoResponseDto> toPlanoDtoList(List<Plano> listPlanos) {

        return listPlanos.stream().map( plano -> toPlanoDto(plano)).collect(Collectors.toList());
    }
}
