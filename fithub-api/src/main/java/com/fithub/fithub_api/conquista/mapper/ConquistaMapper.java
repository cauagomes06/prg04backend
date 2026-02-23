package com.fithub.fithub_api.conquista.mapper;

import com.fithub.fithub_api.conquista.dto.ConquistaGaleriaDto;
import com.fithub.fithub_api.conquista.entity.Conquista;
import com.fithub.fithub_api.conquista.entity.UsuarioConquista;
import org.springframework.stereotype.Component;

@Component
public class ConquistaMapper {

    /**
     * Mapeia uma conquista quando ela AINDA ESTÁ BLOQUEADA para o usuário.
     */
    public ConquistaGaleriaDto toDtoBloqueada(Conquista conquista) {
        return ConquistaGaleriaDto.builder()
                .id(conquista.getId())
                .nome(conquista.getNome())
                .descricao(conquista.getDescricao())
                .icone(conquista.getIcone())
                .tipo(conquista.getTipo())
                .desbloqueada(false)
                .dataDesbloqueio(null)
                .build();
    }

    /**
     * Mapeia uma conquista quando ela JÁ FOI DESBLOQUEADA pelo usuário.
     */
    public ConquistaGaleriaDto toDtoDesbloqueada(UsuarioConquista usuarioConquista) {
        Conquista conquista = usuarioConquista.getConquista();
        return ConquistaGaleriaDto.builder()
                .id(conquista.getId())
                .nome(conquista.getNome())
                .descricao(conquista.getDescricao())
                .icone(conquista.getIcone())
                .tipo(conquista.getTipo())
                .desbloqueada(true)
                .dataDesbloqueio(usuarioConquista.getDataDesbloqueio())
                .build();
    }
}