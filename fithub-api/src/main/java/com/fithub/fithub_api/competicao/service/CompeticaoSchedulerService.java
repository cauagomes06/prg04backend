package com.fithub.fithub_api.competicao.service;

import com.fithub.fithub_api.competicao.entity.Competicao;
import com.fithub.fithub_api.competicao.entity.StatusCompeticao;
import com.fithub.fithub_api.competicao.repository.CompeticaoRepository;
import com.fithub.fithub_api.inscricao.entity.Inscricao;
import com.fithub.fithub_api.inscricao.service.InscricaoService;
import com.fithub.fithub_api.notificacao.service.NotificacaoService;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompeticaoSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(CompeticaoSchedulerService.class);
    private final CompeticaoRepository competicaoRepository;
    private final InscricaoService inscricaoService;
    private final UsuarioRepository usuarioRepository; // 3. Adicione o repositório aqui
    private final NotificacaoService notificacaoService;

    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void verificarEEncerrarCompeticoes() {
        logger.info("Scheduler a correr: Verificando competições para encerrar...");

        List<Competicao> competicoesParaEncerrar = competicaoRepository.findAllByStatusAndDataFimBefore(
                StatusCompeticao.ABERTA,
                LocalDateTime.now()
        );

        if (competicoesParaEncerrar.isEmpty()) {
            logger.info("Nenhuma competição para encerrar.");
            return;
        }

        for (Competicao competicao : competicoesParaEncerrar) {
            logger.info("A encerrar competição ID #{}: {}", competicao.getId(), competicao.getNome());

            List<Inscricao> ranking = inscricaoService.buscarInscricoesPorCompeticaoOrdenado(competicao.getId());

            // --- LÓGICA DE SCORE E NOTIFICAÇÃO ---
            if (!ranking.isEmpty() && ranking.get(0).getResultado() != null) {
                Usuario vencedor = ranking.get(0).getUsuario();

                // Busca os pontos da  competição
                int pontosPelaVitoria = competicao.getPontosVitoria();

                vencedor.setScoreTotal(vencedor.getScoreTotal() + pontosPelaVitoria);
                usuarioRepository.save(vencedor);

                // 6. LÓGICA DE NOTIFICAÇÃO
                String link = "/portal/competicoes/" + competicao.getId();
                notificacaoService.criarNotificacao(
                        vencedor,
                        "Parabéns! Você venceu a competição '" + competicao.getNome() + "' e ganhou " + pontosPelaVitoria + " pontos!",
                        link
                );
            }

            competicao.setStatus(StatusCompeticao.ENCERRADA);
            competicaoRepository.save(competicao);
            logger.info("Competição ID #{} encerrada com sucesso.", competicao.getId());
        }
    }
}
