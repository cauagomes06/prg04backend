package com.fithub.fithub_api.exercicio.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExercicioResponseDto {

    private Long id;

    private String nome;

    private String descricao;

    private String grupoMuscular;

    private String urlVideo;
}
