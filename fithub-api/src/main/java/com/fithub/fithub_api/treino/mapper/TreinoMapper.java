package com.fithub.fithub_api.treino.mapper;

import com.fithub.fithub_api.itemtreino.entity.ItemTreino;
import com.fithub.fithub_api.treino.dto.ItemTreinoResponseDto;
import com.fithub.fithub_api.treino.dto.TreinoCreateDto;
import com.fithub.fithub_api.treino.dto.TreinoResponseDto;
import com.fithub.fithub_api.treino.entity.Treino;
import com.fithub.fithub_api.usuario.entity.Usuario;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TreinoMapper {

    private static final ModelMapper mapper = new ModelMapper();

    public static Treino toTreino(TreinoCreateDto treinoCreateDto) {
        return mapper.map(treinoCreateDto, Treino.class);
    }

    // Método Base: Converte Treino -> DTO (Sem saber quem está logado)
    public static TreinoResponseDto toDto(Treino treino) {
        TreinoResponseDto dto = new TreinoResponseDto();

        // 1. Dados Básicos
        dto.setId(treino.getId());
        dto.setNome(treino.getNome());
        dto.setDescricao(treino.getDescricao());
        dto.setMediaNota(treino.getMediaNota());
        dto.setTotalAvaliacoes(treino.getTotalAvaliacoes());

        if (treino.getStatus() != null) {
            dto.setStatus(treino.getStatus().name());
        }

        // 2. Dados do Criador (com proteção contra Null)
        if (treino.getCriador() != null) {
            dto.setCriadorId(treino.getCriador().getId());
            dto.setCriadorFoto(treino.getCriador().getFotoUrl());

            // Tenta pegar o nome da Pessoa, se não tiver, pega o Username
            if (treino.getCriador().getPessoa() != null) {
                // Ajustado para 'getNomeCompleto()' conforme sua entidade Pessoa
                dto.setCriadorNome(treino.getCriador().getPessoa().getNomeCompleto());
            } else {
                dto.setCriadorNome(treino.getCriador().getUsername());
            }
        }

        // 3. Mapeamento Manual dos Itens de Treino
        List<ItemTreinoResponseDto> itensDto = new ArrayList<>();
        if (treino.getItensTreino() != null) {
            for (ItemTreino item : treino.getItensTreino()) {
                ItemTreinoResponseDto itemDto = new ItemTreinoResponseDto();

                itemDto.setId(item.getId());
                itemDto.setSeries(item.getSeries());
                itemDto.setRepeticoes(item.getRepeticoes());
                itemDto.setDescanso(item.getDescanso());
                itemDto.setOrdem(item.getOrdem());


                // Dados do Exercício
                if (item.getExercicio() != null) {
                    itemDto.setNomeExercicio(item.getExercicio().getNome());
                    itemDto.setExercicioId(item.getExercicio().getId());
                }

                itensDto.add(itemDto);
            }
        }
        dto.setItems(itensDto);

        // 4. Estatísticas Sociais (Requer o campo alunosSeguidores na entidade Treino)
        if (treino.getAlunosSeguidores() != null) {
            dto.setNumeroSeguidores(treino.getAlunosSeguidores().size());
        } else {
            dto.setNumeroSeguidores(0);
        }

        // Padrão false
        dto.setSeguindo(false);

        return dto;
    }

    // Método Com Contexto: Converte Treino -> DTO (Sabendo quem está logado)
    public static TreinoResponseDto toDto(Treino treino, Usuario usuarioLogado) {
        // Reaproveita a lógica base
        TreinoResponseDto dto = toDto(treino);

        // Verifica se o usuário logado está na lista de seguidores
        if (usuarioLogado != null && treino.getAlunosSeguidores() != null) {
            boolean isFollowing = treino.getAlunosSeguidores().contains(usuarioLogado);
            dto.setSeguindo(isFollowing);
        }

        return dto;
    }

    // Listas
    public static List<TreinoResponseDto> toListDto(List<Treino> treinos) {
        return treinos.stream()
                .map(TreinoMapper::toDto)
                .collect(Collectors.toList());
    }

    public static List<TreinoResponseDto> toListDto(List<Treino> treinos, Usuario usuarioLogado) {
        return treinos.stream()
                .map(treino -> toDto(treino, usuarioLogado))
                .collect(Collectors.toList());
    }
}