package com.fithub.fithub_api.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UsuarioResponseDto {

    private Long id;
    private String username;

    private PessoaResponseDto pessoa;
    private String nomePlano;
    private String nomePerfil;

}
