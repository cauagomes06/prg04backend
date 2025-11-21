package com.fithub.fithub_api.dashboard.service;

import com.fithub.fithub_api.aula.repository.AulaRepository;
import com.fithub.fithub_api.competicao.entity.StatusCompeticao;
import com.fithub.fithub_api.competicao.repository.CompeticaoRepository;
import com.fithub.fithub_api.dashboard.dto.DashboardStatsDto;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService implements DashboardIService{

    private final UsuarioRepository usuarioRepository;
    private final CompeticaoRepository competicaoRepository;
    private final AulaRepository aulaRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getStats() {
        // 1. Contagem de Utilizadores
        List<Usuario> alunos = usuarioRepository.findAllByPerfilNome("ROLE_CLIENTE");
        List<Usuario> personais = usuarioRepository.findAllByPerfilNome("ROLE_PERSONAL");

        // 2. Cálculo de Receita (Soma dos preços dos planos dos alunos ativos)
        BigDecimal receita = alunos.stream()
                .map(u -> u.getPlano().getPreco())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Competições Ativas (Abertas ou Em Andamento)
        // Nota: Idealmente adicionaríamos um método countByStatusIn no repositório,
        // mas aqui filtramos em memória para não alterar o repo agora.
        long compsAtivas = competicaoRepository.findAll().stream()
                .filter(c -> c.getStatus() == StatusCompeticao.ABERTA || c.getStatus() == StatusCompeticao.EM_ANDAMENTO)
                .count();

        // 4. Aulas Futuras
        long aulasFuturas = aulaRepository.findAll().stream()
                .filter(a -> a.getDataHoraInicio().isAfter(LocalDateTime.now()))
                .count();

        return DashboardStatsDto.builder()
                .totalAlunos((long) alunos.size())
                .totalPersonais((long) personais.size())
                .competicoesAtivas(compsAtivas)
                .aulasAgendadas(aulasFuturas)
                .receitaEstimadaMensal(receita)
                .build();
    }
}