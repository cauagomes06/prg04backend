package com.fithub.fithub_api.usuario.service;

import com.fithub.fithub_api.usuario.entity.StatusPlano;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanoExpiracaoService {

    private final UsuarioRepository usuarioRepository;

    // Roda todos os dias à meia-noite
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void verificarExpiracaoDePlanos() {
        log.info("Iniciando verificação de planos expirados...");

        LocalDate hoje = LocalDate.now();

        // Busca usuários que têm data de vencimento e o plano já venceu
        List<Usuario> usuariosExpirados = usuarioRepository.findAll().stream()
                .filter(u -> u.getDataVencimentoPlano() != null &&
                        u.getDataVencimentoPlano().isBefore(hoje) &&
                        u.getStatusPlano() == StatusPlano.ATIVO)
                .toList();

        for (Usuario usuario : usuariosExpirados) {
            log.info("O plano do usuário {} (ID: {}) expirou em {}. Alterando status para INATIVO.",
                    usuario.getUsername(), usuario.getId(), usuario.getDataVencimentoPlano());

            usuario.setStatusPlano(StatusPlano.INATIVO);

            usuarioRepository.save(usuario);
        }

        log.info("Verificação de expiração finalizada. {} usuários atualizados.", usuariosExpirados.size());
    }
    // Roda todas as manhãs às 09:00
    @Scheduled(cron = "0 0 9 * * *")
    public void avisarVencimentoProximo() {
        // Data de daqui a 3 dias
        LocalDate dataAlvo = LocalDate.now().plusDays(3);

        List<Usuario> aVencer = usuarioRepository.findAll().stream()
                .filter(u -> u.getDataVencimentoPlano() != null &&
                        u.getDataVencimentoPlano().equals(dataAlvo) &&
                        u.getStatusPlano() == StatusPlano.ATIVO)
                .toList();

        for (Usuario u : aVencer) {
            // Aqui chamarias o teu NotificacaoService
            log.info("Aviso: O plano do utilizador {} vence em 3 dias.", u.getUsername());
            // notificacaoService.enviar(u, "O teu plano vence em 3 dias! Renova agora para não perderes o acesso.");
        }
    }
}