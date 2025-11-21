package com.fithub.fithub_api.plano.service;

import com.fithub.fithub_api.notificacao.service.NotificacaoService;
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
public class PlanoSchedulerService {

    private final UsuarioRepository usuarioRepository;
    private final NotificacaoService notificacaoService;

    // Executa todos os dias à meia-noite (00:00:00)
    // Cron: segundo minuto hora dia mês dia_semana
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void verificarValidadePlanos() {
        log.info("Scheduler de Planos: A iniciar verificação diária de vencimentos...");

        LocalDate hoje = LocalDate.now();

        // 1. Buscar utilizadores ATIVOS com data de vencimento ANTERIOR a hoje
        List<Usuario> usuariosExpirados = usuarioRepository.findByStatusPlanoAndDataVencimentoPlanoBefore(
                StatusPlano.ATIVO,
                hoje
        );

        if (usuariosExpirados.isEmpty()) {
            log.info("Scheduler de Planos: Nenhum plano expirado encontrado hoje.");
            return;
        }

        // 2. Processar cada utilizador
        for (Usuario usuario : usuariosExpirados) {
            log.info("A inativar plano do utilizador: {} (Venceu em: {})",
                    usuario.getUsername(), usuario.getDataVencimentoPlano());

            // Atualiza o estado para INATIVO
            usuario.setStatusPlano(StatusPlano.INATIVO);

            // Guarda na base de dados
            usuarioRepository.save(usuario);

            // 3. Enviar Notificação ao Utilizador
            try {
                String mensagem = String.format(
                        "O seu plano '%s' expirou no dia %s. Renove agora para continuar a treinar!",
                        usuario.getPlano().getNome(),
                        usuario.getDataVencimentoPlano()
                );

                // Link para a página de perfil ou configuração onde ele pode ver o plano
                String link = "/portal/perfil";

                notificacaoService.criarNotificacao(usuario, mensagem, link);

            } catch (Exception e) {
                log.error("Erro ao enviar notificação de expiração para o utilizador {}: {}", usuario.getId(), e.getMessage());
            }
        }

        log.info("Scheduler de Planos: Processamento concluído. Total inativados: {}", usuariosExpirados.size());
    }
}