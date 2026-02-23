package com.fithub.fithub_api.conquista.controller;


import com.fithub.fithub_api.conquista.dto.ConquistaGaleriaDto;
import com.fithub.fithub_api.conquista.service.ConquistaIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/conquistas")
@RequiredArgsConstructor
public class ConquistaController {

    private final ConquistaIService  conquistaService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ConquistaGaleriaDto>> obterGaleria(@PathVariable Long usuarioId) {
        List<ConquistaGaleriaDto> galeria = conquistaService.obterGaleriaDoUsuario(usuarioId);

        return ResponseEntity.ok(galeria);
    }
}
