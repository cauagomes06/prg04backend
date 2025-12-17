package com.fithub.fithub_api.pagamento.controller;

import com.fithub.fithub_api.pagamento.service.MercadoPagoService; // Importe o seu Service
import lombok.RequiredArgsConstructor; // Import do Lombok
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/webhooks/mercadopago")
@RequiredArgsConstructor
public class WebhookController {

    private final MercadoPagoService mercadoPagoService;

    @PostMapping
    public ResponseEntity<String> handleNotification(@RequestParam Map<String, String> params, @RequestBody(required = false) String body) {
        System.out.println("Webhook Recebido! Parâmetros: " + params);

        String paymentId = params.get("data.id");
        if (paymentId == null) {
            paymentId = params.get("id");
        }

        if (paymentId == null) {
            System.out.println(" Aviso: Recebi um webhook sem ID. Ignorando.");
            return ResponseEntity.ok().build();
        }

        try {
            Long id = Long.parseLong(paymentId);
            System.out.println(" ID Identificado: " + id);

            // Agora isso vai funcionar porque o service foi injetado!
            String status = mercadoPagoService.consultarPagamento(id);
            System.out.println("Status atual no MP: " + status);

            return ResponseEntity.ok().build();

        } catch (NumberFormatException e) {
            System.err.println(" Erro ao converter ID: " + paymentId);
            return ResponseEntity.ok().build();
        }
    }
}