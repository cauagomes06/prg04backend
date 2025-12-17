package com.fithub.fithub_api.pagamento.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/pagamentos") // Padronize o prefixo
public class PagamentoWebhookController {

    @GetMapping("/sucesso")
    public ResponseEntity<Void> sucesso() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:5173/sucesso")) // URL do seu React
                .build();
    }

    @GetMapping("/falha")
    public ResponseEntity<Void> falha() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:5173/falha"))
                .build();
    }
}
