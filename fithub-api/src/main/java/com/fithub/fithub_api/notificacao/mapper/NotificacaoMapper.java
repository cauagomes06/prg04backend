package com.fithub.fithub_api.notificacao.mapper;

import com.fithub.fithub_api.notificacao.dto.NotificacaoResponseDto;
import com.fithub.fithub_api.notificacao.entity.Notificacao;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class NotificacaoMapper {

    private static final ModelMapper mapper = new ModelMapper();

    public static Notificacao toNotificacao(NotificacaoResponseDto notificacaoResponseDto) {
        return mapper.map(notificacaoResponseDto, Notificacao.class);
    }
    public static NotificacaoResponseDto toNotificacaoDto(Notificacao notificacao) {
        return mapper.map(notificacao, NotificacaoResponseDto.class);
    }
    public static List<NotificacaoResponseDto> toListNotificacoesDto(List<Notificacao> notificacoes) {
        return notificacoes.stream().map( NotificacaoMapper::toNotificacaoDto).collect(Collectors.toList());
    }
}
