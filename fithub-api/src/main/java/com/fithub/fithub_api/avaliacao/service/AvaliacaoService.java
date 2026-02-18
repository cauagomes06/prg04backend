package com.fithub.fithub_api.avaliacao.service;

import com.fithub.fithub_api.avaliacao.dto.AvaliacaoCreateDto;
import com.fithub.fithub_api.avaliacao.entity.Avaliacao;
import com.fithub.fithub_api.avaliacao.repository.AvaliacaoRepository;
import com.fithub.fithub_api.treino.entity.Treino;
import com.fithub.fithub_api.treino.repository.TreinoRepository;
import com.fithub.fithub_api.usuario.entity.Usuario;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AvaliacaoService implements  AvaliacaoIService{

    private final AvaliacaoRepository avaliacaoRepository;
    private final TreinoRepository treinoRepository;

    @Transactional
    @Override
    public void avaliarTreino(Long treinoId, AvaliacaoCreateDto dto, Usuario usuario) {
        Treino treino = treinoRepository.findById(treinoId)
                .orElseThrow(() -> new EntityNotFoundException("Treino não encontrado"));

        // Regra: Dono não avalia o próprio treino
        if (treino.getCriador().equals(usuario)) {
            throw new IllegalArgumentException("Você não pode avaliar seu próprio treino.");
        }

        // 1. Verifica se já existe avaliação deste usuário para este treino
        Optional<Avaliacao> avaliacaoExistente = avaliacaoRepository.findByTreinoIdAndUsuarioId(treinoId, usuario.getId());

        Avaliacao avaliacao;
        if (avaliacaoExistente.isPresent()) {
            // Atualiza existente
            avaliacao = avaliacaoExistente.get();
            avaliacao.setNota(dto.getNota());
        } else {
            // Cria nova
            avaliacao = Avaliacao.builder()
                    .treino(treino)
                    .usuario(usuario)
                    .nota(dto.getNota())
                    .build();
        }

        avaliacaoRepository.save(avaliacao);

        // 2. Atualizar Cache de Média no Treino (Performance)
        atualizarMediaDoTreino(treino);
    }
    private void atualizarMediaDoTreino(Treino treino) {
        Double media = avaliacaoRepository.calcularMediaPorTreino(treino.getId());
        long total = avaliacaoRepository.countByTreinoId(treino.getId());

        treino.setMediaNota(media != null ? media : 0.0);
        treino.setTotalAvaliacoes((int) total);

        treinoRepository.save(treino);
    }
}
