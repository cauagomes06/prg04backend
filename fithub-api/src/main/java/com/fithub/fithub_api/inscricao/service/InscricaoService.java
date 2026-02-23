package com.fithub.fithub_api.inscricao.service;

import com.fithub.fithub_api.competicao.entity.Competicao;
import com.fithub.fithub_api.competicao.entity.StatusCompeticao;
import com.fithub.fithub_api.competicao.entity.TipoDeOrdenacao;
import com.fithub.fithub_api.competicao.service.CompeticaoIService;
import com.fithub.fithub_api.conquista.service.ConquistaIService;
import com.fithub.fithub_api.exception.EntityNotFoundException;
import com.fithub.fithub_api.inscricao.exception.InscricaoConflictException;
import com.fithub.fithub_api.inscricao.dto.InscricaoResponseDto;
import com.fithub.fithub_api.inscricao.dto.ResultadoSubmitDto;
import com.fithub.fithub_api.inscricao.entity.Inscricao;
import com.fithub.fithub_api.inscricao.mapper.InscricaoMapper;
import com.fithub.fithub_api.inscricao.repository.InscricaoRepository;
import com.fithub.fithub_api.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InscricaoService implements InscricaoIService {

    private final InscricaoRepository inscricaoRepository;
    private final CompeticaoIService competicaoIService;
    private final ConquistaIService conquistaIService;

    @Override
    @Transactional
    public Inscricao inscreverEmCompeticao(Long idCompeticao, Usuario usuarioLogado) {

        Competicao competicao = competicaoIService.buscarPorId(idCompeticao);

        // 1. Validações de negócio
        if(inscricaoRepository.existsByUsuarioIdAndCompeticaoId(usuarioLogado.getId(), idCompeticao)){
            throw new InscricaoConflictException("Você já está inscrito na competição: " + competicao.getNome());
        }

        if (competicao.getDataFim().isBefore(LocalDateTime.now()) || competicao.getStatus() != StatusCompeticao.ABERTA){
            throw new RuntimeException("As inscrições já foram encerradas");
        }

        // 2. Cria e salva a inscrição
        Inscricao inscricao = new Inscricao();
        inscricao.setUsuario(usuarioLogado);
        inscricao.setCompeticao(competicao);

        Inscricao inscricaoSalva = inscricaoRepository.save(inscricao);

        // 🎯 GATILHO DE CONQUISTAS ===================================================
        try {
            // Conta quantas competições o usuário já participou (incluindo esta)
            long totalParticipacoes = inscricaoRepository.countByUsuarioId(usuarioLogado.getId());

            // Dispara o processamento para a métrica de participações
            conquistaIService.processarProgresso(usuarioLogado, "COMPETICOES_PARTICIPOU", (double) totalParticipacoes);

        } catch (Exception e) {
            // Log de erro silencioso para não cancelar a inscrição do usuário
            System.err.println("Erro ao processar conquista de participação: " + e.getMessage());
        }
        // ============================================================================

        return inscricaoSalva;
    }

    @Override
    @Transactional
    public Inscricao enviarResultado(Long idInscricao, ResultadoSubmitDto dto, Usuario usuarioLogado) {

        Inscricao inscricao = inscricaoRepository.findById(idInscricao).orElseThrow(
                () -> new EntityNotFoundException("Inscricao com id #" + idInscricao+" nao encontrado"));

        if (!inscricao.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Voce não tem permissao para submeter resultado");
        }

        if (inscricao.getCompeticao().getDataFim().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Competição ja terminou");
        }

        if(inscricao.getCompeticao().getStatus() != StatusCompeticao.ABERTA
                && inscricao.getCompeticao().getStatus() != StatusCompeticao.EM_ANDAMENTO){
            throw new RuntimeException("Esta competição está encerrada. Não é possível submeter novos resultados.");
        }
        inscricao.setResultado(dto.getResultado());
        inscricao.setDataSubmissao(LocalDateTime.now());

        return inscricaoRepository.save(inscricao);
    }

    @Override
    @Transactional
    public void cancelarInscricao(Long idInscricao, Usuario usuarioLogado) {

        Inscricao inscricao = inscricaoRepository.findById(idInscricao).orElseThrow(
                () -> new EntityNotFoundException("Inscricao com id #" + idInscricao+" nao encontrado"));

     //  só o dono da inscrição ( um administrador pode cancelar
        boolean isAdmin = usuarioLogado.getPerfil().getNome().equals("ROLE_ADMIN");
        boolean isOwner = inscricao.getUsuario().getId().equals(usuarioLogado.getId());

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Você não tem permissão para cancelar esta inscrição.");
        }

        inscricaoRepository.delete(inscricao);
    }

    @Override
    public List<InscricaoResponseDto> buscarInscricoesPorUsuario(Usuario usuario) {
        List<Inscricao> inscricoes = inscricaoRepository.findAllByUsuarioId(usuario.getId());

        // O seu InscricaoMapper já tem o método 'toListDto'
        return InscricaoMapper.toListDto(inscricoes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inscricao> buscarInscricoesPorCompeticaoOrdenado(Long idCompeticao) {

        //  verifica se a competição existe
        Competicao competicao = competicaoIService.buscarPorId(idCompeticao);

        // Busca a lista de inscrições
        List<Inscricao> inscricoes = inscricaoRepository.findByCompeticaoId(idCompeticao);


        Comparator<Inscricao> comparador = (inscricaoA, inscricaoB) -> {
            // Converte os resultados (ex: "120 kg", "20:15") para números
            double resultadoA = parseResultado(inscricaoA.getResultado());
            double resultadoB = parseResultado(inscricaoB.getResultado());

            if (competicao.getTipoOrdenacao() == TipoDeOrdenacao.MAIOR_MELHOR) {
                // Ordenação decrescente (o maior número fica em primeiro)
                return Double.compare(resultadoB, resultadoA);
            } else {
                // Ordenação crescente (o menor número fica em primeiro)
                return Double.compare(resultadoA, resultadoB);
            }
        };

        // Aplica o comparador à lista
        inscricoes.sort(comparador);

        return inscricoes;
    }


    /**
     * Método auxiliar (privado) para "limpar" a string de resultado
     * e convertê-la num número para podermos comparar.
     */
    private double parseResultado(String resultadoStr) {
        if (resultadoStr == null || resultadoStr.isBlank()) {
            return 0;
        }

        // Tenta converter "20:15" (minutos:segundos) para segundos
        if (resultadoStr.contains(":")) {
            try {
                String[] partes = resultadoStr.split(":");
                double minutos = Double.parseDouble(partes[0].replaceAll("[^\\d.]", ""));
                double segundos = Double.parseDouble(partes[1].replaceAll("[^\\d.]", ""));
                return (minutos * 60) + segundos;
            } catch (Exception e) {
                return 0; // Falha ao converter tempo
            }
        }

        // Tenta converter "120 kg" ou "500 reps" para um número
        try {
            // Remove tudo o que não for dígito ou ponto decimal
            String numeroLimpo = resultadoStr.replaceAll("[^\\d.]", "");
            if (numeroLimpo.isEmpty()) {
                return 0;
            }
            return Double.parseDouble(numeroLimpo);
        } catch (NumberFormatException e) {
            return 0; // Falha ao converter número
        }
    }
}
