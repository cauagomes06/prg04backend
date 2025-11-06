package com.fithub.fithub_api.aula.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AulaCreateDto {

    @NotBlank(message = "O nome da aula não pode ser vazio.")
    private String nome;

    private String descricao;

    @NotNull(message = "O ID do instrutor é obrigatório.")
    private Long instrutorIdentificador; // O ID do Usuário com perfil ROLE_PERSONAL

    @NotNull(message = "A data e hora de início não podem ser nulas.")
    @Future(message = "A aula deve ser agendada para uma data futura.")
    private LocalDateTime dataHoraInicio;

    @NotNull(message = "A duração é obrigatória.")
    @Min(value = 10, message = "A duração deve ser de pelo menos 10 minutos.")
    private Integer duracaoMinutos;

    @NotNull(message = "O número de vagas é obrigatório.")
    @Min(value = 1, message = "Deve haver pelo menos 1 vaga.")
    private Integer vagasTotais;
}
