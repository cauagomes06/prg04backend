package com.fithub.fithub_api.execucao.dto.execucao;


import com.fithub.fithub_api.execucao.dto.item.ItemExecucaoCreateDto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ExecucaoCreateDto {

    @NotNull
    private Long treinoId;

    @NotNull
    private LocalDateTime dataInicio;

    @NotNull
    private LocalDateTime dataFim;

    private String observacoesGerais;

    @NotNull
    private List<ItemExecucaoCreateDto> itens;
}
