package com.fithub.fithub_api.web.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExercicioCreateDto {

    @NotEmpty
    private String nome;
    private String descricao;
    @NotEmpty
    private String grupoMuscular;
    private String urlVideo;
}
