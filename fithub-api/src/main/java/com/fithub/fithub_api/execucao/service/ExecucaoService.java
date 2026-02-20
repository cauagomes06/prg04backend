package com.fithub.fithub_api.execucao.service;

import com.fithub.fithub_api.execucao.dto.execucao.ExecucaoCreateDto;
import com.fithub.fithub_api.execucao.dto.execucao.TreinoExecucaoResponseDto;
import com.fithub.fithub_api.execucao.entity.ItemExecucao;
import com.fithub.fithub_api.execucao.entity.TreinoExecucao;
import com.fithub.fithub_api.execucao.mapper.ExecucaoMapper;
import com.fithub.fithub_api.execucao.repository.ExecucaoRepository;
import com.fithub.fithub_api.exercicio.repository.ExercicoRepository;
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
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ExecucaoService  implements  ExecucaoIService{
    private final ExecucaoRepository execucaoRepository;
    private final TreinoRepository treinoRepository;
    private final ExercicoRepository exercicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ExecucaoMapper execucaoMapper;
    private final SecurityUtils securityUtils; // Para pegar o usuário logado

    @Override
    @Transactional
    public TreinoExecucaoResponseDto registrarExecucao(ExecucaoCreateDto dto) {
        Usuario usuario = securityUtils.getUsuarioLogado();
        Treino treino = treinoRepository.findById(dto.getTreinoId())
                .orElseThrow(() -> new EntityNotFoundException("Treino não encontrado"));

        TreinoExecucao execucao = execucaoMapper.toEntity(dto);
        execucao.setUsuario(usuario);
        execucao.setTreino(treino);

        // 1. Cálculo da duração
        long segundos = Duration.between(dto.getDataInicio(), dto.getDataFim()).getSeconds();
        execucao.setDuracaoSegundos(segundos);

        // 2. Processamento dos Itens e Pontos Base
        int pontosTotais = 0;
        execucao.setItens(new ArrayList<>());

        for (var itemDto : dto.getItens()) {
            ItemExecucao item = new ItemExecucao();
            item.setTreinoExecucao(execucao);
            item.setExercicio(exercicoRepository.findById(itemDto.getExercicioId())
                    .orElseThrow(() -> new EntityNotFoundException("Exercício não encontrado")));
            item.setSeriesConcluidas(itemDto.getSeriesConcluidas());

            execucao.getItens().add(item);

            // Regra simples: 10 pontos por série concluída
            pontosTotais += (itemDto.getSeriesConcluidas() * 10);
        }

        execucao.setPontosGanhos(pontosTotais);

        int scoreTotal =  usuario.getScoreTotal();
        usuario.setScoreTotal(usuario.getScoreTotal() + pontosTotais);


        // 3. Salvar
        usuarioRepository.save(usuario);
        TreinoExecucao salva = execucaoRepository.save(execucao);



        return execucaoMapper.toResponseDto(salva);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TreinoExecucaoResponseDto> buscarHistoricoPaginado(Long usuarioId, Pageable pageable) {
        return execucaoRepository.findByUsuarioId(usuarioId, pageable)
                .map(execucaoMapper::toResponseDto);
    }
}
