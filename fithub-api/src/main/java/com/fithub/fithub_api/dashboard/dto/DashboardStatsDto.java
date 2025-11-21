package com.fithub.fithub_api.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {

    private Long totalAlunos;
    private Long totalPersonais;
    private Long competicoesAtivas;
    private Long aulasAgendadas;
    private BigDecimal receitaEstimadaMensal;
}