package com.fithub.fithub_api.competicao.dto;

import com.fithub.fithub_api.competicao.entity.TipoDeOrdenacao;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@Getter
@Setter
public class CompeticaoCreateDto {

    @NotBlank(message = "O nome da competição não pode ser vazio.")
    private String nome;

    @NotBlank(message = "A descrição não pode ser vazia.")
    private String descricao;

    @NotNull(message = "A data de início é obrigatória.")
    @FutureOrPresent(message = "A data de início não pode ser no passado.")
    private LocalDateTime dataInicio;

    @NotNull(message = "A data de fim é obrigatória.")
    @FutureOrPresent(message = "A data de fim não pode ser no passado.")
    private LocalDateTime dataFim;

    @NotNull(message = "O tipo de ordenação é obrigatório.")
    private TipoDeOrdenacao tipoOrdenacao;

    @NotNull(message = "A pontuação é obrigatória.")
    @Min(value = 0, message = "A pontuação não pode ser negativa.")
    private Integer pontosVitoria;
}
