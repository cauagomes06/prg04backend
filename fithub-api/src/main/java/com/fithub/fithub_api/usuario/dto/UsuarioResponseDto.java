package com.fithub.fithub_api.usuario.dto;

import com.fithub.fithub_api.pessoa.dto.PessoaResponseDto;
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
