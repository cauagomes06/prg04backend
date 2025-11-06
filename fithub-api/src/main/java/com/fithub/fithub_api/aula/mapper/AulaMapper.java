package com.fithub.fithub_api.aula.mapper;

import com.fithub.fithub_api.aula.dto.AulaCreateDto;
import com.fithub.fithub_api.aula.dto.AulaResponseDto;
import com.fithub.fithub_api.aula.dto.InstrutorResponseDto;
import com.fithub.fithub_api.aula.entity.Aula;

import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class AulaMapper {


    public static Aula toAula (AulaCreateDto aulaCreateDto) {
        return new ModelMapper().map(aulaCreateDto, Aula.class);
    }

    public static AulaResponseDto toAulaDto (Aula aula) {

        ModelMapper modelMapper = new ModelMapper();
        AulaResponseDto responseDto = modelMapper.map(aula, AulaResponseDto.class);

        if(aula.getInstrutor() != null && aula.getInstrutor().getPessoa().getNomeCompleto() != null){
            responseDto.setInstrutor(modelMapper.map(aula.getInstrutor().getPessoa(), InstrutorResponseDto.class));
        }

        int vagasOcupadas =(aula.getReservas() != null)?aula.getReservas().size():0;// se vaga for nulo preenche como 0
        int vagasDisponiveis = aula.getVagasTotais() - vagasOcupadas;

        responseDto.setVagasDisponiveis(vagasDisponiveis);

        return responseDto;
    }

    public static List<AulaResponseDto> toAulaDtoList (List<Aula> aulas) {

        return  aulas.stream().map(AulaMapper::toAulaDto).collect(Collectors.toList());
    }
}
