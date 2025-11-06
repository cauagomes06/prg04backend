package com.fithub.fithub_api.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UsuarioSenhaDto {
    @NotBlank
    @Size(min = 6, max = 100)
    private String senhaAtual;
    @NotBlank
    @Size(min = 6, max = 100)
    private String novaSenha;
    @NotBlank
    @Size(min = 6, max = 100)
    private String confirmaSenha;
}
