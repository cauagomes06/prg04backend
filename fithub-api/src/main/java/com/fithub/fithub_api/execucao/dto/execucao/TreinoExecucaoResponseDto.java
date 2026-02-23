package com.fithub.fithub_api.execucao.dto.execucao;


import com.fithub.fithub_api.conquista.dto.ConquistaGaleriaDto;
import com.fithub.fithub_api.execucao.dto.item.ItemExecucaoResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TreinoExecucaoResponseDto {

    private Long id;
    private Long treinoId;
    private String nomeTreino;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Long duracaoSegundos;
    private boolean subiuDeNivel;
    private Integer nivelAtual;
    private Integer pontosGanhos;
    private String observacoesGerais;
    private List<ItemExecucaoResponseDto> itens;
    private List<ConquistaGaleriaDto> conquistas;
}
