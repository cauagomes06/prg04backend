package com.fithub.fithub_api.competicao.service;

import com.fithub.fithub_api.competicao.entity.Competicao;
import com.fithub.fithub_api.competicao.entity.StatusCompeticao;
import com.fithub.fithub_api.competicao.repository.CompeticaoRepository;
import com.fithub.fithub_api.conquista.service.ConquistaIService;
import com.fithub.fithub_api.inscricao.entity.Inscricao;
import com.fithub.fithub_api.inscricao.repository.InscricaoRepository;
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
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompeticaoSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(CompeticaoSchedulerService.class);
    private final CompeticaoRepository competicaoRepository;
    private final InscricaoService inscricaoService;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoService notificacaoService;
    private final ConquistaIService conquistaIService; // Injetado
    private final InscricaoRepository inscricaoRepository; // Injetado

    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void verificarEEncerrarCompeticoes() {
        logger.info("Scheduler a correr: Verificando competições para encerrar...");

        List<StatusCompeticao> statusAtivos = Arrays.asList(
                StatusCompeticao.ABERTA,
                StatusCompeticao.EM_ANDAMENTO
        );

        List<Competicao> competicoesParaEncerrar = competicaoRepository.findByStatusInAndDataFimBefore(
                statusAtivos,
                LocalDateTime.now()
        );

        if (competicoesParaEncerrar.isEmpty()) return;

        for (Competicao competicao : competicoesParaEncerrar) {
            logger.info("A encerrar competição ID #{}: {}", competicao.getId(), competicao.getNome());

            List<Inscricao> ranking = inscricaoService.buscarInscricoesPorCompeticaoOrdenado(competicao.getId());

            if (!ranking.isEmpty()) {
                double[] distribuicao = {1.0, 0.7, 0.5, 0.3, 0.1};
                int totalPremiados = Math.min(ranking.size(), 5);

                for (int i = 0; i < totalPremiados; i++) {
                    Inscricao inscricao = ranking.get(i);

                    if (inscricao.getResultado() != null) {
                        Usuario participante = inscricao.getUsuario();

                        // --- MARCAR O VENCEDOR OFICIAL ---
                        if (i == 0) {
                            inscricao.setVencedor(true);
                            inscricaoRepository.save(inscricao);

                            // GATILHO DE CONQUISTA PARA O CAMPEÃO
                            try {
                                long totalVitorias = inscricaoRepository.countByUsuarioIdAndVencedorTrue(participante.getId());
                                conquistaIService.processarProgresso(participante, "COMPETICOES_VENCIDAS", (double) totalVitorias);
                            } catch (Exception e) {
                                logger.error("Erro ao processar conquista de vitória: ", e.getMessage());
                            }
                        }

                        // --- PREMIAÇÃO ---
                        int pontosGanhos = (int) (competicao.getPontosVitoria() * distribuicao[i]);

                        if (pontosGanhos > 0) {
                            int scoreAtual =     participante.getScoreTotal();
                            participante.setScoreTotal(scoreAtual + pontosGanhos);
                            usuarioRepository.save(participante);

                            // Notificação
                            try {
                                String mensagem = String.format("Parabéns! Ficou em %dº lugar na competição '%s' e ganhou %d pontos!",
                                        (i + 1), competicao.getNome(), pontosGanhos);
                                notificacaoService.criarNotificacao(participante, mensagem, "/portal/competicoes");
                            } catch (Exception e) {
                                logger.error("Erro ao enviar notificação: ", e);
                            }
                        }
                    }
                }
            }

            competicao.setStatus(StatusCompeticao.ENCERRADA);
            competicaoRepository.save(competicao);
        }
    }
}