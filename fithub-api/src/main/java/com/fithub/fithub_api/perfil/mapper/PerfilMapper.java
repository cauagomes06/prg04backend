package com.fithub.fithub_api.perfil.mapper;


import com.fithub.fithub_api.perfil.entity.Perfil;
import com.fithub.fithub_api.perfil.dto.PerfilCreateDto;
import com.fithub.fithub_api.perfil.dto.PerfilResponseDto;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class PerfilMapper {


    public static Perfil toPerfil(PerfilCreateDto perfilCreateDto) {
        return new ModelMapper().map(perfilCreateDto, Perfil.class);
    }

    public static PerfilResponseDto toPerfilDto(Perfil perfil) {
        return new ModelMapper().map(perfil, PerfilResponseDto.class);
    }
    public static List<PerfilResponseDto> toPerfilDtoList(List<Perfil> perfilList) {
        return perfilList.stream().map(PerfilMapper::toPerfilDto).collect(Collectors.toList());
    }
}
