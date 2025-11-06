package com.fithub.fithub_api.inscricao.mapper;


import com.fithub.fithub_api.inscricao.dto.InscricaoResponseDto;
import com.fithub.fithub_api.inscricao.entity.Inscricao;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class InscricaoMapper {


    private static final ModelMapper MAPPER;

    // Bloco estático para inicializar o mapper (se não usar injeção)
    static {
        MAPPER = new ModelMapper();
        // Configuração para mapear o 'id' da Inscrição para 'idInscricao' no DTO
        MAPPER.createTypeMap(Inscricao.class, InscricaoResponseDto.class)
                .addMappings(mapper -> mapper.map(
                        Inscricao::getId, // Origem
                        InscricaoResponseDto::setIdInscricao // Destino
                ));
    }
    public static InscricaoResponseDto toDto(Inscricao inscricao) {
        // 1. Mapeia os campos fáceis (id -> idInscricao, resultado, dataSubmissao)
        InscricaoResponseDto dto = MAPPER.map(inscricao, InscricaoResponseDto.class);

        // 2. Mapeia manualmente os dados aninhados do Usuario
        if (inscricao.getUsuario() != null) {
            dto.setUsuarioId(inscricao.getUsuario().getId());

            // 3. Busca o nome a partir da entidade Pessoa associada
            if (inscricao.getUsuario().getPessoa() != null) {
                dto.setNomeUsuario(inscricao.getUsuario().getPessoa().getNomeCompleto());
            }
        }

        return dto;
    }
    public static List<InscricaoResponseDto> toListDto(List<Inscricao> inscricoes) {
        return inscricoes.stream()
                .map(InscricaoMapper::toDto)
                .collect(Collectors.toList());
    }
}
