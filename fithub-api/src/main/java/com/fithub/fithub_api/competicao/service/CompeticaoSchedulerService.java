package com.fithub.fithub_api.competicao.service;

import com.fithub.fithub_api.competicao.entity.Competicao;
import com.fithub.fithub_api.competicao.entity.StatusCompeticao;
import com.fithub.fithub_api.competicao.repository.CompeticaoRepository;
import com.fithub.fithub_api.inscricao.entity.Inscricao;
import com.fithub.fithub_api.inscricao.service.InscricaoService;
import com.fithub.fithub_api.usuario.entity.Usuario;
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

    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void verificarEEncerrarCompeticoes() {
        logger.info("Scheduler a correr: Verificando competições para encerrar...");

        // 1. Busca no banco todas as competições que estão ABERTAS e cuja data final JÁ PASSOU
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

            // 3. Obtém o ranking final (o serviço já ordena a lista)
            List<Inscricao> ranking = inscricaoService.buscarInscricoesPorCompeticaoOrdenado(competicao.getId());

            // 4. LÓGICA DE NEGÓCIO PÓS-COMPETIÇÃO
            // Exemplo 1: Enviar notificação para o vencedor (o primeiro da lista)
            if (!ranking.isEmpty()) {
                Usuario vencedor = ranking.get(0).getUsuario();
                logger.info("O vencedor da competição '{}' é: {}", competicao.getNome(), vencedor.getUsername());
                // notificacaoService.enviarNotificacao(vencedor, "Parabéns! Você venceu...");
            }

            // 5. Atualiza o status da competição para "ENCERRADA"
            competicao.setStatus(StatusCompeticao.ENCERRADA);
            competicaoRepository.save(competicao);

            logger.info("Competição ID #{} encerrada com sucesso.", competicao.getId());
        }
    }
}
