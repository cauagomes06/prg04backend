package com.fithub.fithub_api.reserva.repository;

import com.fithub.fithub_api.reserva.entity.Reserva;
import com.fithub.fithub_api.reserva.enums.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    boolean existsByUsuarioIdAndAulaId(Long usuarioId, Long aulaId);

    int countByAulaId(Long aulaId);

    List<Reserva> findByUsuarioId(Long usuarioId);

    // Substitui o findByAulaIdAndPresenteTrue
    List<Reserva> findByAulaIdAndStatus(Long aulaId, StatusReserva status);

    // Busca quem não compareceu para o Scheduler marcar falta
    List<Reserva> findByAulaIdAndStatusNot(Long aulaId, StatusReserva status);

    // Conta presenças confirmadas para as conquistas
    long countByUsuarioIdAndStatus(Long usuarioId, StatusReserva status);
}