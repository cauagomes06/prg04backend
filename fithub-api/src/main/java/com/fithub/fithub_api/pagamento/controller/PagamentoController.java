package com.fithub.fithub_api.pagamento.controller;

import com.fithub.fithub_api.pagamento.dto.PagamentoRequestDto;
import com.fithub.fithub_api.pagamento.dto.PagamentoResponseDto;
import com.fithub.fithub_api.pagamento.repository.PagamentoRepository;
import com.fithub.fithub_api.pagamento.service.MercadoPagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller responsável por gerenciar o fluxo de pagamentos do FitHub via Mercado Pago.
 */
@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final MercadoPagoService mercadoPagoService;
    private final PagamentoRepository pagamentoRepository;

    /**
     * ENDPOINT DE CRIAÇÃO (POST):
     * Recebe o ID do usuário e o ID do plano e gera o link de pagamento.
     * Resolve o erro 404 ao tentar iniciar o checkout.
     */
    @PostMapping("/checkout")
    public ResponseEntity<PagamentoResponseDto> criarCheckout(@RequestBody @Valid PagamentoRequestDto dto) {
        // Chama o serviço para criar a preferência no Mercado Pago com external_reference
        PagamentoResponseDto response = mercadoPagoService.criarPreferencia(
                dto.getUsuarioId(),
                dto.getPlanoId()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * ENDPOINT DE RETORNO / PONTE (GET):
     * Recebe o usuário vindo do Mercado Pago, processa o status e redireciona para o Front.
     * Liberado no SecurityConfig para permitir acesso externo sem token.
     */
    @GetMapping("/ponte")
    public ResponseEntity<Void> pontePagamento(
            @RequestParam("status") String status,
            @RequestParam("external_reference") String externalReference,
            @RequestParam(value = "payment_id", required = false) Long paymentId
    ) {
        // 1. Processa a aprovação e atualiza o plano no banco se o status for sucesso
        if ("sucesso".equalsIgnoreCase(status) || "approved".equalsIgnoreCase(status)) {
            // O método consultarPagamento já chama internamente o usuarioService.atualizarPlanoUsuario
            mercadoPagoService.consultarPagamento(paymentId);

            // 2. Redireciona o navegador para a página de sucesso no React (Vite/Porta 5173)
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:5173/sucesso"))
                    .build();
        }

        // 3. Caso o pagamento falhe ou seja cancelado, redireciona para a página de falha
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:5173/falha"))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/relatorio/faturamento")
    public ResponseEntity<Map<String, Object>> obterRelatorioFaturamento() {
        BigDecimal total = pagamentoRepository.calcularTotalFaturado();
        long totalVendas = pagamentoRepository.count();

        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("total_faturado", total != null ? total : 0);
        relatorio.put("quantidade_vendas", totalVendas);
        relatorio.put("moeda", "BRL");

        return ResponseEntity.ok(relatorio);
    }
}