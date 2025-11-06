package com.fithub.fithub_api.reserva.mapper;

import com.fithub.fithub_api.reserva.entity.Reserva;
import com.fithub.fithub_api.reserva.dto.ReservaResponseDto;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class ReservaMapper {

    private static final ModelMapper mapper = new ModelMapper();

    public static ReservaResponseDto toDto(Reserva reserva) {
        ReservaResponseDto dto = mapper.map(reserva, ReservaResponseDto.class);

        dto.setId(reserva.getId());
        dto.setAulaId(reserva.getAula().getId());
        dto.setDataHoraAula(reserva.getAula().getDataHoraInicio());

        if (reserva.getAula().getInstrutor() != null && reserva.getAula().getInstrutor().getPessoa() != null) {
            dto.setNomeInstrutor(reserva.getAula().getInstrutor().getPessoa().getNomeCompleto());
        }
        return dto;
    }
    public static List<ReservaResponseDto> toListDto(List<Reserva> reservas) {
        return reservas.stream().map(ReservaMapper::toDto).collect(Collectors.toList());

    }
}
