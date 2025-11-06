package com.fithub.fithub_api.plano.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlanoResponseDto {

    Long idPlano;
    String nomePlano;
    BigDecimal preco;
    String descricaoPlano;
}
