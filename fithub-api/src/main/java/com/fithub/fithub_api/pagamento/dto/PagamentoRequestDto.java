package com.fithub.fithub_api.pagamento.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PagamentoRequestDto {

    @NotNull(message = "O ID do usuário é obrigatório para processar o pagamento.")
    private Long usuarioId;

    @NotNull(message = "O ID do plano deve ser informado.")
    private Long planoId;
}
