package com.fithub.fithub_api.pagamento.service;

import com.fithub.fithub_api.pagamento.dto.PagamentoResponseDto;
import com.fithub.fithub_api.pagamento.entity.Pagamento;
import com.fithub.fithub_api.pagamento.repository.PagamentoRepository;
import com.fithub.fithub_api.plano.entity.Plano;
import com.fithub.fithub_api.plano.service.PlanoService;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.service.UsuarioService;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MercadoPagoService {

    private final PagamentoRepository pagamentoRepository;

    private final PlanoService planoService;
    private final UsuarioService usuarioService;

    // Token secreto da sua conta Mercado Pago (definido no application.properties)
    @Value("${mercadopago.access.token}")
    private String accessToken;

    // URL pública (Ngrok) necessária para o Mercado Pago conseguir devolver o usuário
    @Value("${app.api.public.url:https://malisa-defiable-fae.ngrok-free.dev}")
    private String apiPublicUrl;

    // URL opcional para notificações automáticas (Webhooks)
    @Value("${app.backend.url:}")
    private String backendUrl;

    /**
     * Inicializa a configuração do SDK do Mercado Pago assim que o Spring sobe o serviço.
     */
    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    /**
     * Cria uma 'Preferência de Pagamento'.
     * É este método que gera o link azul do Mercado Pago onde o usuário insere o cartão.
     */
    public PagamentoResponseDto criarPreferencia(Long usuarioId, Long planoId) {
        try {
            // 1. Busca os detalhes do plano (preço, nome) no seu banco de dados
            Plano plano = planoService.buscarPlanoById(planoId);

            // 2. Monta o item que aparecerá no carrinho do Mercado Pago
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(plano.getId().toString())
                    .title(plano.getNome())
                    .description(plano.getDescricao())
                    .quantity(1)
                    .currencyId("BRL")
                    .unitPrice(plano.getPreco())
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            // 3. Define para onde o usuário será levado após pagar.
            // Aqui usamos a 'ponte' para garantir que o backend processe a aprovação antes de ir para o front.
            String urlPonte = apiPublicUrl + "/api/pagamentos/ponte";

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(urlPonte + "?status=sucesso")
                    .failure(urlPonte + "?status=falha")
                    .pending(urlPonte + "?status=pendente")
                    .build();

            // 4. Configura dados fictícios do pagador (necessário para o ambiente de testes/sandbox)
            PreferencePayerRequest payerRequest = PreferencePayerRequest.builder()
                    .name("Tester")
                    .surname("Silva")
                    .email("test_user_123456@testuser.com")
                    .identification(IdentificationRequest.builder().type("CPF").number("02839418042").build())
                    .build();

            // 5. Constrói a requisição final
            PreferenceRequest.PreferenceRequestBuilder preferenceBuilder = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .payer(payerRequest)
                    .autoReturn("approved") // Retorna automaticamente se o pagamento for aprovado

                    // A "CHAVE": Guarda quem está comprando o quê para recuperar depois
                    .externalReference(usuarioId.toString() + "_" + planoId.toString());

            // 6. Configura o Webhook se o backend estiver exposto na internet
            if (backendUrl != null && !backendUrl.trim().isEmpty()) {
                preferenceBuilder.notificationUrl(backendUrl + "/api/webhooks/mercadopago");
            }

            // 7. Envia a requisição para a API do Mercado Pago
            Preference preference = new PreferenceClient().create(preferenceBuilder.build());

            // 8. Retorna apenas o link de pagamento (init_point) para o seu Frontend
            return new PagamentoResponseDto(preference.getInitPoint());

        } catch (Exception e) {
            log.error("Erro ao criar preferência MP", e);
            throw new RuntimeException("Erro ao processar pagamento", e);
        }
    }

    /**
     * Consulta o status de um pagamento específico usando o ID gerado pelo Mercado Pago.
     */
    public String consultarPagamento(Long paymentId) {
        try {
            Payment payment = new PaymentClient().get(paymentId);
            if (payment != null) {
                String status = payment.getStatus();

                if ("approved".equals(status)) {

                    processarAprovacao(
                            payment.getExternalReference(), // 1. IDs do banco
                            payment.getId(),                // 2. ID do Mercado Pago
                            status,                         // 3. Status
                            payment.getTransactionAmount(), // 4. Valor (BigDecimal)
                            payment.getDescription()        // 5. Nome do Plano
                    );
                }
                return status;
            }
            return "not_found";
        } catch (Exception e) {
            log.error("Erro ao consultar pagamento: {}", e.getMessage());
            return "error";
        }
    }

    private void processarAprovacao(String externalReference, Long paymentId, String status, BigDecimal valor, String tituloPlano) {
        try {
            // 1. Verificação de Idempotência: Já processamos esse pagamento?
            if (pagamentoRepository.findByMercadoPagoId(paymentId.toString()).isPresent()) {
                log.info("Pagamento {} já foi processado anteriormente. Ignorando duplicata.", paymentId);
                return;
            }

            String[] parts = externalReference.split("_");
            Long usuarioId = Long.parseLong(parts[0]);
            Long planoId = Long.parseLong(parts[1]);

            // 2. Busca o usuário para associar ao pagamento
            Usuario usuario = usuarioService.buscarPorId(usuarioId);

            // 3. Salva o histórico de pagamento
            Pagamento novoPagamento = Pagamento.builder()
                    .usuario(usuario)
                    .valor(valor)
                    .status(status)
                    .mercadoPagoId(paymentId.toString())
                    .planoNome(tituloPlano)
                    .build();
            pagamentoRepository.save(novoPagamento);

            // 4. Ativa o plano do usuário
            usuarioService.atualizarPlanoUsuario(usuarioId, planoId);

            log.info("Sucesso: Pagamento registrado e plano ativado para o usuário {}", usuarioId);

        } catch (Exception e) {
            log.error("Erro ao registrar pagamento e ativar plano: {}", e.getMessage());
        }
    }
}