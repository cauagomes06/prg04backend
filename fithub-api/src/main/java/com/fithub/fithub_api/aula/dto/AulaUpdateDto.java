package com.fithub.fithub_api.aula.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AulaUpdateDto {


    @Size(min = 3, message = "O nome deve ter pelo menos 3 caracteres.")
    private String nome;

    private String descricao;

    private Long instrutorIdentificador;

    @Future(message = "A aula deve ser agendada para uma data futura.")
    private LocalDateTime dataHoraInicio;

    @Min(value = 1, message = "A duração deve ser de pelo menos 1 minuto.")
    private Integer duracaoMinutos;

    @Min(value = 1, message = "Deve haver pelo menos 1 vaga.")
    private Integer vagasTotais;
}
