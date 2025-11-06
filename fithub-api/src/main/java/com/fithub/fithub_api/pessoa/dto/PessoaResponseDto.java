package com.fithub.fithub_api.pessoa.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PessoaResponseDto {

    private String nomeCompleto;
    private String telefone;
    //nao inclui cpf pois é dado sensivel
}
