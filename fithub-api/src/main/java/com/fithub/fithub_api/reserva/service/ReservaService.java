package com.fithub.fithub_api.reserva.service;

import com.fithub.fithub_api.aula.entity.Aula;
import com.fithub.fithub_api.aula.enums.StatusAula;
import com.fithub.fithub_api.aula.service.AulaService;
import com.fithub.fithub_api.conquista.enums.TipoMetrica;
import com.fithub.fithub_api.conquista.service.ConquistaIService;

import com.fithub.fithub_api.exception.EntityNotFoundException;
import com.fithub.fithub_api.gamificacao.service.GamificacaoIService;
import com.fithub.fithub_api.reserva.enums.StatusReserva;
import com.fithub.fithub_api.reserva.exception.ReservaUniqueException;
import com.fithub.fithub_api.reserva.entity.Reserva;
import com.fithub.fithub_api.reserva.repository.ReservaRepository;
import com.fithub.fithub_api.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservaService implements ReservaIService {

    private final ReservaRepository reservaRepository;
    private final AulaService aulaService;
    private final ConquistaIService conquistaIService;
    private final GamificacaoIService gamificacaoIService;

    @Override
    @Transactional
    public Reserva criarReserva(Long idAula, Usuario usuarioLogado) {

        Aula aula = aulaService.buscarPorId(idAula);

        // REGRA 1: Não pode reservar aula que já acabou ou foi cancelada
        if (aula.getStatus() != StatusAula.AGENDADA) {
            throw new IllegalArgumentException("Esta aula já foi encerrada ou cancelada.");
        }

        if(reservaRepository.existsByUsuarioIdAndAulaId(usuarioLogado.getId(), idAula)){
            throw new ReservaUniqueException("Usuario ja reservou esta aula");
        }

        // REGRA 2: Só conta as vagas de quem REALMENTE está na aula (ignora CANCELADA)
        // OBS: Você precisará alterar no repositório para ignorar status cancelado, senão aulas parecerão cheias
        int qtdVagasOcupadas = aula.getReservas().stream()
                .filter(r -> r.getStatus() != StatusReserva.CANCELADA)
                .toList().size();

        if(qtdVagasOcupadas >= aula.getVagasTotais()){
            throw new ReservaUniqueException("Aula esgotada. Não há vagas disponiveis");
        }

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuarioLogado);
        reserva.setAula(aula);
        reserva.setStatus(StatusReserva.AGENDADA);

        return reservaRepository.save(reserva);
    }

    @Override
    @Transactional
    public void cancelarReserva(Long idReserva, Usuario usuarioLogado) {

        Reserva reserva = reservaRepository.findById(idReserva).orElseThrow(
                () -> new EntityNotFoundException("Reserva com id #" + idReserva + " nao encontrada")
        );

        if (!reserva.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Não é permitido cancelar reserva de outro usuario");
        }

        // REGRA 3: Não pode cancelar se já tiver levado falta ou ganhado presença
        if (reserva.getStatus() == StatusReserva.CONFIRMADA || reserva.getStatus() == StatusReserva.FALTOU) {
            throw new IllegalArgumentException("Não é possível cancelar uma reserva que já ocorreu.");
        }

        // Muda o status em vez de deletar para manter o histórico
        reserva.setStatus(StatusReserva.CANCELADA);
        reservaRepository.save(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> buscarMinhasReservas(Usuario usuarioLogado) {
        return reservaRepository.findByUsuarioId(usuarioLogado.getId());
    }

    // ==========================================================
    // NOVO: MÉTODO PARA O INSTRUTOR DAR CHECK-IN (Gatilho de XP)
    // ==========================================================
    @Override
    @Transactional
    public void realizarCheckIn(Long idReserva, Usuario usuarioLogado) {
        Reserva reserva = reservaRepository.findById(idReserva).orElseThrow(
                () -> new EntityNotFoundException("Reserva com id #" + idReserva + " nao encontrada")
        );

        // Validação de permissão (Instrutor ou Admin)
        boolean isInstrutor = reserva.getAula().getInstrutor().getId().equals(usuarioLogado.getId());
        boolean isAdmin = usuarioLogado.getPerfil().getNome().equals("ROLE_PERSONAL");

        if (!isInstrutor && !isAdmin) {
            throw new AccessDeniedException("Apenas o instrutor responsável pode realizar o check-in.");
        }

        // 1. Atualiza o Status
        reserva.setStatus(StatusReserva.CONFIRMADA);
        reservaRepository.save(reserva);

        // =========================================================
        // 2. GATILHOS DE GAMIFICAÇÃO E CONQUISTAS
        // =========================================================
        Usuario aluno = reserva.getUsuario();

        // Dá o XP da aula imediatamente
        gamificacaoIService.adicionarPontos(aluno, 50);

        // Conta o histórico de presenças e avalia se ganhou medalha
        long totalAulas = reservaRepository.countByUsuarioIdAndStatus(aluno.getId(), StatusReserva.CONFIRMADA);
        conquistaIService.processarProgresso(aluno, TipoMetrica.AULAS_PARTICIPADAS, (double) totalAulas);

        log.info("Check-in realizado! Aluno {} ganhou XP e avaliou conquistas.", aluno.getUsername());
    }
}