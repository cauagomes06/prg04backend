package com.fithub.fithub_api.reserva.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservaCreateDto {

    @NotNull(message = "O ID da aula é obrigatório.")
    private Long aulaId;
}