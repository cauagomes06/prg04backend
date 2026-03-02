package com.fithub.fithub_api.aula.repository;

import com.fithub.fithub_api.aula.entity.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AulaRepository extends JpaRepository<Aula, Long> {

    @Query("SELECT a FROM Aula a WHERE YEAR(a.dataHoraInicio) = :ano AND MONTH(a.dataHoraInicio) = :mes")
    List<Aula> findByAnoEMes(Integer ano, Integer mes);

    List<Aula> findByInstrutorId(Long instrutorId);

    // MUDANÇA AQUI: Query nativa compatível com PostgreSQL
    @Query(value = "SELECT * FROM aulas WHERE (data_hora_inicio + duracao_minutos * interval '1 minute') < :agora AND status = :status", nativeQuery = true)
    List<Aula> findAulasTerminadasParaProcessar(@Param("agora") LocalDateTime agora, @Param("status") String status);

    // (Caso já tenha adicionado o método de contar do instrutor, mantenha-o)
    long countByInstrutorIdAndStatus(Long instrutorId, String status);
}