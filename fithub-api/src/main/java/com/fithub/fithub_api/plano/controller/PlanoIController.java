package com.fithub.fithub_api.plano.controller;

import com.fithub.fithub_api.web.dto.PlanoCreateDto;
import com.fithub.fithub_api.web.dto.PlanoResponseDto;
import com.fithub.fithub_api.web.dto.PlanoUpdateDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface PlanoIController {


    public ResponseEntity<PlanoResponseDto> registrarPlano(@RequestBody PlanoCreateDto planoCreateDto);

    public ResponseEntity<Void> deletePlano(Long id);

    public ResponseEntity<List<PlanoResponseDto>> listarPlanos();

    public ResponseEntity<PlanoResponseDto> buscarPlanoByid(Long id);

    public ResponseEntity<PlanoResponseDto> editarPlano(@PathVariable Long id,@RequestBody @Valid PlanoUpdateDto updateDto);
    }
