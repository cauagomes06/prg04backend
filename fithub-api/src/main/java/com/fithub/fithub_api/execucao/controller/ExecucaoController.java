package com.fithub.fithub_api.execucao.controller;


import com.fithub.fithub_api.execucao.dto.execucao.ExecucaoCreateDto;
import com.fithub.fithub_api.execucao.dto.execucao.TreinoExecucaoResponseDto;
import com.fithub.fithub_api.execucao.service.ExecucaoIService;
import com.fithub.fithub_api.infraestructure.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/execucoes")
public class ExecucaoController  {

    private final ExecucaoIService execucaoService;
    private final SecurityUtils securityUtils;


    @PostMapping("/finalizar")
    public ResponseEntity<TreinoExecucaoResponseDto> finalizarTreino(@RequestBody @Valid ExecucaoCreateDto dto) {
        TreinoExecucaoResponseDto response = execucaoService.registrarExecucao(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/meu-historico")
    public ResponseEntity<Page<TreinoExecucaoResponseDto>> getMeusTreinos(
            @PageableDefault(size = 10, sort = "dataInicio") Pageable pageable) {

        Long usuarioId = securityUtils.getUsuarioLogado().getId();
        Page<TreinoExecucaoResponseDto> historico = execucaoService.buscarHistoricoPaginado(usuarioId, pageable);

        return ResponseEntity.ok(historico);
    }
}
