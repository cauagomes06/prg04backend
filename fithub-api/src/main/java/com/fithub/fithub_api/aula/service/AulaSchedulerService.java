package com.fithub.fithub_api.aula.service;

import com.fithub.fithub_api.aula.entity.Aula;
import com.fithub.fithub_api.aula.enums.StatusAula;
import com.fithub.fithub_api.aula.repository.AulaRepository;
import com.fithub.fithub_api.conquista.enums.TipoMetrica;
import com.fithub.fithub_api.reserva.entity.Reserva;
import com.fithub.fithub_api.reserva.enums.StatusReserva;
import com.fithub.fithub_api.reserva.repository.ReservaRepository;
import com.fithub.fithub_api.conquista.service.ConquistaService;
import com.fithub.fithub_api.gamificacao.service.GamificacaoService;
import com.fithub.fithub_api.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AulaSchedulerService {

    private final AulaRepository aulaRepository;
    private final ReservaRepository reservaRepository;
    private final ConquistaService conquistaService;
    private final GamificacaoService gamificacaoService;

    @Scheduled(cron = "0 0/30 * * * *") // Executa a cada 30 minutos
    @Transactional
    public void processarAulasEncerradas() {
        log.info("Iniciando processamento automático de aulas encerradas...");

        LocalDateTime agora = LocalDateTime.now();

        // Busca aulas que já terminaram (usando a query de DATE_ADD que criamos no Repo)
        List<Aula> aulasParaEncerrar = aulaRepository.findAulasTerminadasParaProcessar(agora, StatusAula.AGENDADA.name());
        for (Aula aula : aulasParaEncerrar) {
            encerrarAulaEProcessarRecompensas(aula);
        }

        log.info("Processamento de aulas concluído. {} aulas finalizadas.", aulasParaEncerrar.size());
    }

    private void encerrarAulaEProcessarRecompensas(Aula aula) {
        // 1. Busca apenas quem teve a presença CONFIRMADA (check-in feito)
        List<Reserva> presentes = reservaRepository.findByAulaIdAndStatus(aula.getId(), StatusReserva.CONFIRMADA);

        for (Reserva reserva : presentes) {
            var usuario = reserva.getUsuario();

            // Adiciona XP (Gamificação)
            gamificacaoService.adicionarPontos(usuario, 50);

            // Conta total de presenças confirmadas para disparar Conquistas
            long totalAulas = reservaRepository.countByUsuarioIdAndStatus(usuario.getId(), StatusReserva.CONFIRMADA);
            conquistaService.processarProgresso(usuario, TipoMetrica.AULAS_PARTICIPADAS, (double) totalAulas);
        }

        // 2. Marca como "FALTOU" todos que tinham reserva AGENDADA mas não confirmaram presença
        List<Reserva> ausentes = reservaRepository.findByAulaIdAndStatus(aula.getId(), StatusReserva.AGENDADA);
        for (Reserva reserva : ausentes) {
            reserva.setStatus(StatusReserva.FALTOU);
            reservaRepository.save(reserva);
        }

        // 3. Finaliza o ciclo da aula
        aula.setStatus(StatusAula.CONCLUIDA);
        aulaRepository.save(aula);
        // =========================================================
        // 2. GATILHO DE CONQUISTA PARA O INSTRUTOR
        // =========================================================
        Usuario instrutor = aula.getInstrutor();

        // Conta quantas aulas este instrutor já deu e que estão concluídas
        long aulasDadas = aulaRepository.countByInstrutorIdAndStatus(instrutor.getId(), StatusAula.CONCLUIDA.name());

        // Dá XP pela aula ministrada (ex: 100 XP)
        gamificacaoService.adicionarPontos(instrutor, 100);

        // Dispara o avaliador de conquistas para o professor
        conquistaService.processarProgresso(instrutor, TipoMetrica.AULAS_MINISTRADAS, (double) aulasDadas);

        log.info("Aula ID {} encerrada. Instrutor {} recebeu os pontos.", aula.getId(), instrutor.getUsername());

        log.info("Aula ID {} encerrada. Presentes: {} | Faltas: {}",
                aula.getId(), presentes.size(), ausentes.size());
    }
}