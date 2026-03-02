package com.fithub.fithub_api.aula.mapper;

import com.fithub.fithub_api.aula.dto.AulaCreateDto;
import com.fithub.fithub_api.aula.dto.AulaResponseDto;
import com.fithub.fithub_api.aula.dto.InstrutorResponseDto;
import com.fithub.fithub_api.aula.entity.Aula;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class AulaMapper {

    // Instância única para otimizar performance (Singleton)
    private static final ModelMapper modelMapper = new ModelMapper();

    public static Aula toAula (AulaCreateDto aulaCreateDto) {
        return modelMapper.map(aulaCreateDto, Aula.class);
    }

    public static AulaResponseDto toAulaDto (Aula aula) {
        AulaResponseDto responseDto = modelMapper.map(aula, AulaResponseDto.class);

        if(aula.getInstrutor() != null && aula.getInstrutor().getPessoa() != null){
            responseDto.setInstrutor(modelMapper.map(aula.getInstrutor().getPessoa(), InstrutorResponseDto.class));
        }

        // REGRA DE VAGAS: Conta apenas reservas que NÃO foram canceladas
        long vagasOcupadas = (aula.getReservas() != null) ?
                aula.getReservas().stream()
                        .filter(r -> !r.getStatus().name().equals("CANCELADA"))
                        .count() : 0;

        int vagasDisponiveis = aula.getVagasTotais() - (int) vagasOcupadas;
        responseDto.setVagasDisponiveis(vagasDisponiveis);

        // NOVO: Envia o status para o frontend
        if (aula.getStatus() != null) {
            responseDto.setStatus(aula.getStatus().name());
        }

        return responseDto;
    }

    public static List<AulaResponseDto> toAulaDtoList (List<Aula> aulas) {
        return aulas.stream().map(AulaMapper::toAulaDto).collect(Collectors.toList());
    }
}