package com.fithub.fithub_api.notificacao.service;

import com.fithub.fithub_api.notificacao.entity.Notificacao;
import com.fithub.fithub_api.usuario.entity.Usuario;

import java.util.List;

public interface NotificacaoIService {

    public void criarNotificacao(Usuario destinatario, String mensagem, String link);

    public List<Notificacao> buscarNotificacoesDoUsuario(Usuario usuarioLogado);

    public int contarNotificacoesNaoLidas(Usuario usuarioLogado);

    public Notificacao marcarComoLida(Long idNotificacao, Usuario usuarioLogado);
}
