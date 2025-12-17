package com.fithub.fithub_api.pagamento.repository;

import com.fithub.fithub_api.pagamento.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    // Para evitar processar o mesmo pagamento duas vezes (Idempotência)
    Optional<Pagamento> findByMercadoPagoId(String mercadoPagoId);

    // Consulta para o Relatório: Soma de todos os pagamentos aprovados
    @Query("SELECT SUM(p.valor) FROM Pagamento p WHERE p.status = 'approved' OR p.status = 'approved'")
    BigDecimal calcularTotalFaturado();
}