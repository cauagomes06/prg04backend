package com.fithub.fithub_api.plano.controller;


import com.fithub.fithub_api.plano.entity.Plano;
import com.fithub.fithub_api.plano.service.PlanoService;
import com.fithub.fithub_api.plano.dto.PlanoCreateDto;
import com.fithub.fithub_api.plano.dto.PlanoResponseDto;
import com.fithub.fithub_api.plano.dto.PlanoUpdateDto;
import com.fithub.fithub_api.plano.mapper.PlanoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/planos")
public class PlanoController implements PlanoIController{
    private final PlanoService planoService;


    @Override
    @PostMapping("/register")
    public ResponseEntity<PlanoResponseDto> registrarPlano(PlanoCreateDto planoCreateDto) {
        Plano novoPlano = planoService.registrarPlano(PlanoMapper.toPlano(planoCreateDto));

        return ResponseEntity.status(HttpStatus.CREATED).body(PlanoMapper.toPlanoDto(novoPlano));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<PlanoResponseDto>> listarPlanos() {

        List<Plano> listPlano = planoService.buscarPlanos();
        return ResponseEntity.ok(PlanoMapper.toPlanoDtoList(listPlano));
    }

    @Override
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePlano(@PathVariable Long id) {
        planoService.deletePlano(id);
        return  ResponseEntity.noContent().build();
    }
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PlanoResponseDto> buscarPlanoByid(@PathVariable Long id) {
        Plano plano = planoService.buscarPlanoById(id);

        return ResponseEntity.ok(PlanoMapper.toPlanoDto(plano));
    }
    @Override
    @PutMapping("/update/{id}")
    public ResponseEntity<PlanoResponseDto> editarPlano(
            @PathVariable Long id,@RequestBody @Valid PlanoUpdateDto updateDto){

        PlanoResponseDto planoAtulizado =  planoService.editarPlano(id,updateDto);
        return ResponseEntity.ok(planoAtulizado);
    }

}
