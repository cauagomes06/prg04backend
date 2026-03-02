package com.fithub.fithub_api.aula.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

    @Getter @Setter
    @AllArgsConstructor @NoArgsConstructor
    public class ParticipanteDto {
        private Long idUsuario;
        private String nomeCompleto;
        private String statusReserva;
    }