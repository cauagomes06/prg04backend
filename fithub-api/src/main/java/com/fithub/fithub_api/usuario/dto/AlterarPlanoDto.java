package com.fithub.fithub_api.usuario.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlterarPlanoDto {
    @NotNull(message = "O ID do plano é obrigatório")
    private Long planoId;
}
