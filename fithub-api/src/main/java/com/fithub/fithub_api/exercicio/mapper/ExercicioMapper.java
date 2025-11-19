package com.fithub.fithub_api.exercicio.mapper;

import com.fithub.fithub_api.exercicio.dto.ExercicioCreateDto;
import com.fithub.fithub_api.exercicio.dto.ExercicioResponseDto;
import com.fithub.fithub_api.exercicio.entity.Exercicio;

import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExercicioMapper {

    public static Exercicio toExercicio (ExercicioCreateDto exercicioCreateDto) {
        return new ModelMapper().map(exercicioCreateDto, Exercicio.class);
    }
    public static ExercicioResponseDto toExercicioDto (Exercicio exercicio) {
        if (exercicio == null) {
            return null;
        }
        return new ModelMapper().map(exercicio, ExercicioResponseDto.class);
    }
    public static List<ExercicioResponseDto> toExercicioListDto (List<Exercicio> exercicios) {
        return exercicios.stream()
                .filter(Objects::nonNull).map(ExercicioMapper::toExercicioDto).collect(Collectors.toList());
    }
}
