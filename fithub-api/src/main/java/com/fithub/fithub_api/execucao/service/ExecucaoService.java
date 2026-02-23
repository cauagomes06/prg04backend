package com.fithub.fithub_api.execucao.service;

import com.fithub.fithub_api.conquista.service.ConquistaIService;
import com.fithub.fithub_api.execucao.dto.execucao.ExecucaoCreateDto;
import com.fithub.fithub_api.execucao.dto.execucao.TreinoExecucaoResponseDto;
import com.fithub.fithub_api.execucao.entity.ItemExecucao;
import com.fithub.fithub_api.execucao.entity.TreinoExecucao;
import com.fithub.fithub_api.execucao.mapper.ExecucaoMapper;
import com.fithub.fithub_api.execucao.repository.ExecucaoRepository;
import com.fithub.fithub_api.exercicio.repository.ExercicoRepository;
import com.fithub.fithub_api.gamificacao.service.GamificacaoIService;
import com.fithub.fithub_api.infraestructure.SecurityUtils;
import com.fithub.fithub_api.treino.entity.Treino;
import com.fithub.fithub_api.treino.repository.TreinoRepository;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

    @Service
    @RequiredArgsConstructor
    public class ExecucaoService implements ExecucaoIService {

        private final ExecucaoRepository execucaoRepository;
        private final TreinoRepository treinoRepository;
        private final ExercicoRepository exercicoRepository;
        private final UsuarioRepository usuarioRepository;
        private final GamificacaoIService gamificacaoIService;
        private final ExecucaoMapper execucaoMapper;
        private final SecurityUtils securityUtils;
        private final ConquistaIService conquistaIService;

        private static final int XP_POR_NIVEL = 1000;

        @Override
        @Transactional
        public TreinoExecucaoResponseDto registrarExecucao(ExecucaoCreateDto dto) {
            Usuario usuario = securityUtils.getUsuarioLogado();

            // --- PREPARAÇÃO PARA O STREAK ---
            LocalDate hoje = LocalDate.now();
            LocalDate ontem = hoje.minusDays(1);

            // 1. Capturar o nível ANTES da nova pontuação
            int nivelAnterior = (usuario.getScoreTotal() / XP_POR_NIVEL) + 1;

            Treino treino = treinoRepository.findById(dto.getTreinoId())
                    .orElseThrow(() -> new EntityNotFoundException("Treino não encontrado"));

            // Mapeia DTO para Entidade
            TreinoExecucao execucao = execucaoMapper.toEntity(dto);
            execucao.setUsuario(usuario);
            execucao.setTreino(treino);

            // Cálculo de duração
            long segundos = Duration.between(dto.getDataInicio(), dto.getDataFim()).getSeconds();
            execucao.setDuracaoSegundos(segundos);

            // Cálculo de pontos e processamento de itens
            int pontosTotais = 0;
            execucao.setItens(new ArrayList<>());

            for (var itemDto : dto.getItens()) {
                ItemExecucao item = new ItemExecucao();
                item.setTreinoExecucao(execucao);
                item.setExercicio(exercicoRepository.findById(itemDto.getExercicioId())
                        .orElseThrow(() -> new EntityNotFoundException("Exercício não encontrado")));
                item.setSeriesConcluidas(itemDto.getSeriesConcluidas());

                execucao.getItens().add(item);
                pontosTotais += (itemDto.getSeriesConcluidas() * 10);
            }

            execucao.setPontosGanhos(pontosTotais);

            // 2. Atualizar Score e XP (Gamificação)
            gamificacaoIService.adicionarPontos(usuario, pontosTotais);
            int nivelNovo = (usuario.getScoreTotal() / XP_POR_NIVEL) + 1;

            // 3. Salvar a execução no banco
            TreinoExecucao salva = execucaoRepository.save(execucao);

            // 4. Mapear para DTO de resposta
            TreinoExecucaoResponseDto response = execucaoMapper.toResponseDto(salva);
            response.setSubiuDeNivel(nivelNovo > nivelAnterior);
            response.setNivelAtual(nivelNovo);
            response.setPontosGanhos(pontosTotais);

            //  GATILHOS DE CONQUISTAS E STREAK =========================================
            try {
                // Gatilho 1: Total de Treinos
                long totalTreinos = execucaoRepository.countByUsuarioId(usuario.getId());
                conquistaIService.processarProgresso(usuario, "TREINOS_CONCLUIDOS", (double) totalTreinos);

                // Gatilho 2: Lógica de Streak (Sequência)
                if (usuario.getDataUltimoTreino() == null) {
                    usuario.setSequenciaAtual(1);
                } else if (usuario.getDataUltimoTreino().equals(ontem)) {
                    usuario.setSequenciaAtual(usuario.getSequenciaAtual() + 1);
                } else if (!usuario.getDataUltimoTreino().equals(hoje)) {
                    // Se não treinou ontem e não é o mesmo dia, reinicia
                    usuario.setSequenciaAtual(1);
                }

                usuario.setDataUltimoTreino(hoje);
                usuarioRepository.save(usuario);

                // Processa conquistas de sequência
                conquistaIService.processarProgresso(usuario, "SEQUENCIA_DIAS", (double) usuario.getSequenciaAtual());

                //  CAPTURAR CONQUISTAS PARA O FRONT-END
                // Buscamos a galeria e filtramos apenas as que acabaram de ser desbloqueadas (últimos segundos)
                var conquistasRecentes = conquistaIService.obterGaleriaDoUsuario(usuario.getId()).stream()
                        .filter(c -> c.isDesbloqueada() && c.getDataDesbloqueio() != null)
                        .filter(c -> c.getDataDesbloqueio().isAfter(LocalDateTime.now().minusSeconds(10)))
                        .toList();

                response.setConquistas(conquistasRecentes);

            } catch (Exception e) {
                // Log de erro silencioso para não interromper a finalização do treino
                System.err.println("Erro ao processar conquistas/streak (não crítico): " + e.getMessage());
            }
            // ============================================================================

            return response;
        }

        @Override
        @Transactional(readOnly = true)
        public Page<TreinoExecucaoResponseDto> buscarHistoricoPaginado(Long usuarioId, Pageable pageable) {
            return execucaoRepository.findByUsuarioId(usuarioId, pageable)
                    .map(execucaoMapper::toResponseDto);
        }

        @Override
        @Transactional
        public boolean jaTreinouHoje() {
            Usuario usuario = securityUtils.getUsuarioLogado();
            LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
            LocalDateTime fimDia = LocalDate.now().atTime(LocalTime.MAX);

            boolean jaTreinouHoje = execucaoRepository.existsByUsuarioIdAndDataInicioBetween(
                    usuario.getId(), inicioDia, fimDia
            );

            return !jaTreinouHoje;
        }
}