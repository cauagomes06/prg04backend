package com.fithub.fithub_api.conquista.dto;

import com.fithub.fithub_api.conquista.enums.TipoMedalha;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConquistaGaleriaDto {
    private Long id;
    private String nome;
    private String descricao;
    private String icone;
    private TipoMedalha tipo;

    // Flags cruciais para o Front-end
    private boolean desbloqueada;
    private LocalDateTime dataDesbloqueio; // Será null se estiver bloqueada
}