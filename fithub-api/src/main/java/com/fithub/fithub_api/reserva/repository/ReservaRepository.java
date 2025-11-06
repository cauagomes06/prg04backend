package com.fithub.fithub_api.reserva.repository;

import com.fithub.fithub_api.reserva.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository  extends JpaRepository<Reserva,Long> {

    //metodo para verificar se um usuario ja reservou uma aula especifica
    boolean existsByUsuarioIdAndAulaId(Long usuarioId, Long aulaId);

    //metodo para contar quantas reservas uma aula ja tem
    int countByAulaId(Long aulaId);

    //metodo para buscar todas as reservas de um usuario
    List<Reserva> findByUsuarioId(Long usuarioId);
 }
