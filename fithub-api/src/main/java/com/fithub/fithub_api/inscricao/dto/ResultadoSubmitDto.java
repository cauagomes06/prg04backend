package com.fithub.fithub_api.inscricao.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultadoSubmitDto {

    @NotBlank(message = "O resultado não pode ser vazio.")
    private String resultado; //ex: fiz 100 kg no supino,envio e vejo minha posicao na competicao
}
