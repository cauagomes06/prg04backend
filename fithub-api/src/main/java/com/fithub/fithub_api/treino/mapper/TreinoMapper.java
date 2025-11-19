package com.fithub.fithub_api.treino.mapper;

import com.fithub.fithub_api.itemtreino.entity.ItemTreino;
import com.fithub.fithub_api.treino.dto.ItemTreinoResponseDto;
import com.fithub.fithub_api.treino.entity.Treino;
import com.fithub.fithub_api.treino.dto.TreinoCreateDto;
import com.fithub.fithub_api.treino.dto.TreinoResponseDto;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TreinoMapper {

    private static final ModelMapper mapper = new ModelMapper();

    public static Treino toTreino(TreinoCreateDto treinoCreateDto) {
        return mapper.map(treinoCreateDto, Treino.class);
    }
    public static TreinoResponseDto toDto( Treino treino) {

            TreinoResponseDto treinoResponseDto = mapper.map(treino, TreinoResponseDto.class);

        if (treino.getItensTreino() != null) {

            List<ItemTreinoResponseDto> itensDto = new ArrayList<>();

            for (ItemTreino item : treino.getItensTreino()) {

                ItemTreinoResponseDto itemDto = new ModelMapper().map(item, ItemTreinoResponseDto.class);
                itemDto.setNomeExercicio(item.getExercicio().getNome());
                itensDto.add(itemDto);
            }

            treinoResponseDto.setCriadorNome(treino.getCriador().getPessoa().getNomeCompleto());
            treinoResponseDto.setItems(itensDto);
        }else{
            treinoResponseDto.setItems(new ArrayList<>()); // para lista nao ficar nula
        }

        return treinoResponseDto;
    }
    public static List<TreinoResponseDto> toListDto(List<Treino> treinos) {
        return treinos.stream().map(TreinoMapper::toDto).collect(Collectors.toList());
    }
}
