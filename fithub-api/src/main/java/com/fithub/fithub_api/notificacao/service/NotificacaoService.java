package com.fithub.fithub_api.notificacao.service;

import com.fithub.fithub_api.exception.EntityNotFoundException;
import com.fithub.fithub_api.notificacao.dto.NotificacaoBroadcastDto;
import com.fithub.fithub_api.notificacao.entity.Notificacao;
import com.fithub.fithub_api.notificacao.repository.NotificacaoRepository;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoService implements NotificacaoIService {

    private final UsuarioRepository usuarioRepository;
    private final NotificacaoRepository notificacaoRepository;

    @Transactional
    @Override
    public void criarNotificacao(Usuario destinatario, String mensagem, String link) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDestinatario(destinatario);
        notificacao.setMensagem(mensagem);
        notificacao.setLink(link);
        notificacao.setLida(false); // começa como não lida

        notificacaoRepository.save(notificacao);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Notificacao> buscarNotificacoesDoUsuario(Usuario usuarioLogado) {
        return notificacaoRepository.findByDestinatarioIdOrderByDataCriacaoDesc(usuarioLogado.getId());
    }
    @Transactional(readOnly = true)
    @Override
    public int contarNotificacoesNaoLidas(Usuario usuarioLogado) {
        return notificacaoRepository.countByDestinatarioIdAndLidaFalse(usuarioLogado.getId());
    }
    @Transactional
    @Override
    public Notificacao marcarComoLida(Long idNotificacao, Usuario usuarioLogado) {
        //  Busca a notificação
        Notificacao notificacao = notificacaoRepository.findById(idNotificacao)
                .orElseThrow(() -> new EntityNotFoundException("Notificação com ID #" + idNotificacao + " não encontrada."));

        // REGRA DE SEGURANÇA: O usuario é o dono desta notificação?
        if (!notificacao.getDestinatario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Você não tem permissão para ler esta notificação.");
        }

        // 3. Marca como lida e salva
        notificacao.setLida(true);
        return notificacaoRepository.save(notificacao);
    }

    @Override
    @Transactional
    public void enviarParaTodos(NotificacaoBroadcastDto dto) {
        List<Usuario> todosUsuarios = usuarioRepository.findAll();

        for (Usuario usuario : todosUsuarios) {
            criarNotificacao(usuario, dto.getMensagem(), dto.getLink());
        }
    }
}
