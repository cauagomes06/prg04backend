package com.fithub.fithub_api.infraestructure.cliente;

import com.fithub.fithub_api.usuario.dto.UsuarioResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public class SpringClient {

    public static void main(String[] args) {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .defaultHeader(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE)
                .build();

        String response = webClient.get()
                .uri("/api/planos/buscar")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info(response);
    }
}
