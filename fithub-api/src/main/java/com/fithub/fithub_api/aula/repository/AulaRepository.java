package com.fithub.fithub_api.aula.repository;

import com.fithub.fithub_api.aula.entity.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AulaRepository  extends JpaRepository<Aula, Long> {

    @Query("SELECT a FROM Aula a WHERE YEAR(a.dataHoraInicio) = :ano AND MONTH(a.dataHoraInicio) = :mes")
    List<Aula> findByAnoEMes(Integer ano, Integer mes);

    List<Aula> findByInstrutorId(Long instrutorId);
}
