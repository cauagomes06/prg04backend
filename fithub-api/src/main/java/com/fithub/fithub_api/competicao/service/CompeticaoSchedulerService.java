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

    // Executa a cada minuto
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

        if (competicoesParaEncerrar.isEmpty()) {
            return;
        }

        for (Competicao competicao : competicoesParaEncerrar) {
            logger.info("A encerrar competição ID #{}: {}", competicao.getId(), competicao.getNome());

            // Busca o ranking completo ordenado
            List<Inscricao> ranking = inscricaoService.buscarInscricoesPorCompeticaoOrdenado(competicao.getId());

            if (!ranking.isEmpty()) {
                // --- NOVA LÓGICA: PREMIAR TOP 5 ---

                // Percentagens para 1º, 2º, 3º, 4º, 5º
                double[] distribuicao = {1.0, 0.7, 0.5, 0.3, 0.1};

                // Garante que não tenta acessar índices que não existem (se tiver menos de 5 inscritos)
                int totalPremiados = Math.min(ranking.size(), 5);

                for (int i = 0; i < totalPremiados; i++) {
                    Inscricao inscricao = ranking.get(i);

                    // Só recebe pontos quem submeteu um resultado válido
                    if (inscricao.getResultado() != null) {
                        Usuario participante = inscricao.getUsuario();

                        // Calcula os pontos com base na posição (cast para int)
                        int pontosGanhos = (int) (competicao.getPontosVitoria() * distribuicao[i]);

                        // Atualiza o Score do Utilizador
                        if (pontosGanhos > 0) {
                            Integer scoreTotalObj = participante.getScoreTotal();
                            int scoreAtual = (scoreTotalObj == null) ? 0 : scoreTotalObj;

                            participante.setScoreTotal(scoreAtual + pontosGanhos);
                            usuarioRepository.save(participante);

                            logger.info("Posição #{}: {} ganhou {} pontos.", (i + 1), participante.getPessoa().getNomeCompleto(), pontosGanhos);

                            // Envia Notificação Personalizada
                            try {
                                String link = "/portal/competicoes";
                                String mensagem = String.format(
                                        "Parabéns! Ficou em %dº lugar na competição '%s' e ganhou %d pontos!",
                                        (i + 1), competicao.getNome(), pontosGanhos
                                );

                                notificacaoService.criarNotificacao(participante, mensagem, link);
                            } catch (Exception e) {
                                logger.error("Erro ao enviar notificação: ", e);
                            }
                        }
                    }
                }
            } else {
                logger.info("Competição encerrada sem participantes.");
            }

            // Atualiza status para ENCERRADA
            competicao.setStatus(StatusCompeticao.ENCERRADA);
            competicaoRepository.save(competicao);
        }
    }
}