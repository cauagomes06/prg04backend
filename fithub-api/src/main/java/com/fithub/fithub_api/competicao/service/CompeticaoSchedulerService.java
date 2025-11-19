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

    // Executa a cada minuto (para testes é melhor que hora a hora)
    // cron = "0 * * * * ?" -> A cada minuto
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void verificarEEncerrarCompeticoes() {
        logger.info("Scheduler a correr: Verificando competições para encerrar...");

        // Procura por ABERTA ou EM_ANDAMENTO
        List<StatusCompeticao> statusAtivos = Arrays.asList(
                StatusCompeticao.ABERTA,
                StatusCompeticao.EM_ANDAMENTO
        );

        List<Competicao> competicoesParaEncerrar = competicaoRepository.findByStatusInAndDataFimBefore(
                statusAtivos,
                LocalDateTime.now()
        );

        if (competicoesParaEncerrar.isEmpty()) {
            logger.info("Nenhuma competição pendente para encerrar.");
            return;
        }

        for (Competicao competicao : competicoesParaEncerrar) {
            logger.info("A encerrar competição ID #{}: {}", competicao.getId(), competicao.getNome());

            // Busca ranking ordenado
            List<Inscricao> ranking = inscricaoService.buscarInscricoesPorCompeticaoOrdenado(competicao.getId());

            // Verifica se há inscritos e se o primeiro lugar tem resultado
            if (!ranking.isEmpty()) {
                Inscricao vencedorInscricao = ranking.get(0);

                if (vencedorInscricao.getResultado() != null) {
                    Usuario vencedor = vencedorInscricao.getUsuario();
                    int pontosPelaVitoria = competicao.getPontosVitoria();

                    // Atualiza pontos
                    logger.info("Vencedor encontrado: {}. Adicionando {} pontos.", vencedor.getPessoa().getNomeCompleto(), pontosPelaVitoria);

                    // --- CORREÇÃO AQUI ---
                    // Usamos Integer para permitir que aceite null (se for Wrapper) ou faça autoboxing (se for primitivo)
                    Integer scoreTotalObj = vencedor.getScoreTotal();
                    int scoreAtual = (scoreTotalObj == null) ? 0 : scoreTotalObj;

                    vencedor.setScoreTotal(scoreAtual + pontosPelaVitoria);

                    usuarioRepository.save(vencedor);

                    // Envia Notificação
                    try {
                        String link = "/portal/competicoes";
                        notificacaoService.criarNotificacao(
                                vencedor,
                                "Parabéns! Venceu a competição '" + competicao.getNome() + "' e ganhou " + pontosPelaVitoria + " pontos!",
                                link
                        );
                    } catch (Exception e) {
                        logger.error("Erro ao enviar notificação para vencedor: ", e);
                    }
                } else {
                    logger.warn("Competição ID #{} tem inscritos, mas o líder não submeteu resultado.", competicao.getId());
                }
            } else {
                logger.info("Competição ID #{} encerrada sem participantes.", competicao.getId());
            }

            // Atualiza status para ENCERRADA
            competicao.setStatus(StatusCompeticao.ENCERRADA);
            competicaoRepository.save(competicao);
        }
    }
}