package com.fithub.fithub_api.conquista.service;

import com.fithub.fithub_api.conquista.dto.ConquistaGaleriaDto;
import com.fithub.fithub_api.conquista.entity.Conquista;
import com.fithub.fithub_api.conquista.entity.UsuarioConquista;
import com.fithub.fithub_api.conquista.enums.TipoMetrica;
import com.fithub.fithub_api.conquista.mapper.ConquistaMapper;
import com.fithub.fithub_api.conquista.repository.ConquistaRepository;
import com.fithub.fithub_api.conquista.repository.UsuarioConquistaRepository;
import com.fithub.fithub_api.notificacao.entity.Notificacao;
import com.fithub.fithub_api.notificacao.repository.NotificacaoRepository;
import com.fithub.fithub_api.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConquistaService implements ConquistaIService {

    private final ConquistaRepository conquistaRepository;
    private final UsuarioConquistaRepository usuarioConquistaRepository;
    private final ConquistaMapper conquistaMapper;
    private final NotificacaoRepository notificacaoRepository;

    /**
     * Avaliador Universal: Este é o principal ponto de entrada.
     * Varre as regras da métrica e concede medalhas se o valor for atingido.
     */
    @Override
    @Transactional
    public List<Conquista> processarProgresso(Usuario usuario,
                                              TipoMetrica tipoMetrica, Double valorAcumulado) {
        // 1. Busca no banco TODAS as medalhas que dependem dessa métrica
        List<Conquista> conquistasPossiveis = conquistaRepository.findByTipoMetrica(tipoMetrica);
        List<Conquista> novasConquistas = new ArrayList<>();

        // 2. Avalia cada uma delas
        for (Conquista conquista : conquistasPossiveis) {
            if (valorAcumulado >= conquista.getValorNecessario()) {
                // Tenta conceder. Se o método retornar true, significa que o usuário ganhou agora.
                boolean ganhouAgora = concederMedalhaSegura(usuario, conquista);
                if (ganhouAgora) {
                    novasConquistas.add(conquista);
                }
            }
        }
        return novasConquistas;
    }

    /**
     * Método interno que cria a medalha e a notificação de forma segura.
     * @return true se a medalha foi concedida agora, false se o usuário já possuía.
     */
    @Override
    @Transactional
    public boolean concederMedalhaSegura(Usuario usuario, Conquista conquista) {
        // Trava de segurança: impede duplicidade
        boolean jaPossui = usuarioConquistaRepository.existsByUsuarioIdAndConquistaId(usuario.getId(), conquista.getId());

        if (jaPossui) {
            return false;
        }

        // 1. Salva o registro da conquista (dataDesbloqueio preenchida pelo @Builder ou Constructor)
        UsuarioConquista novaConquista = UsuarioConquista.builder()
                .usuario(usuario)
                .conquista(conquista)
                .dataDesbloqueio(LocalDateTime.now())
                .build();

        usuarioConquistaRepository.save(novaConquista);

        // 2. GERA A NOTIFICAÇÃO (Gatilho para o Frontend aparecer o Toast)
        criarNotificacaoConquista(usuario, conquista);

        log.info("🏆 Nova Conquista! Usuário {} (ID: {}) desbloqueou: {}",
                usuario.getUsername(), usuario.getId(), conquista.getNome());

        return true;
    }

    /**
     * Auxiliar para criar o registro de notificação no banco.
     */
    private void criarNotificacaoConquista(Usuario usuario, Conquista conquista) {
        Notificacao notif = new Notificacao();
        notif.setDestinatario(usuario);
        notif.setTitulo("🏆 Nova Conquista Desbloqueada!");
        notif.setMensagem("Parabéns! Você ganhou a medalha: " + conquista.getNome());
        notif.setTipo("CONQUISTA");
        notif.setLida(false);
        // O campo data_criacao é herdado da PersistenceEntity/Auditable
        notificacaoRepository.save(notif);
    }

    /**
     * Monta a Galeria de Troféus (cruzando todas as medalhas com as ganhas pelo usuário).
     */
    @Override
    @Transactional(readOnly = true)
    public List<ConquistaGaleriaDto> obterGaleriaDoUsuario(Long usuarioId) {
        List<Conquista> todasConquistas = conquistaRepository.findAll();
        List<UsuarioConquista> conquistasDoUsuario = usuarioConquistaRepository.findAllByUsuarioIdWithConquista(usuarioId);

        return todasConquistas.stream().map(conquista -> {
            Optional<UsuarioConquista> conquistaDesbloqueada = conquistasDoUsuario.stream()
                    .filter(uc -> uc.getConquista().getId().equals(conquista.getId()))
                    .findFirst();

            if (conquistaDesbloqueada.isPresent()) {
                return conquistaMapper.toDtoDesbloqueada(conquistaDesbloqueada.get());
            } else {
                return conquistaMapper.toDtoBloqueada(conquista);
            }
        }).collect(Collectors.toList());
    }
}