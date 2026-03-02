package com.fithub.fithub_api.reserva.dto;

import com.fithub.fithub_api.reserva.enums.StatusReserva;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservaResponseDto {

    private Long id;
    private Long aulaId;
    private String nomeAula;
    private LocalDateTime dataHoraAula;
    private String nomeInstrutor;
    private StatusReserva  statusReserva;

}
