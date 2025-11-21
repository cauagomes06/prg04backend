package com.fithub.fithub_api.plano.controller;


import com.fithub.fithub_api.plano.dto.PlanoTrocaDto;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/planos")
public class PlanoController implements PlanoIController{
    private final PlanoService planoService;


    @Override
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanoResponseDto> registrarPlano(@RequestBody @Valid PlanoCreateDto planoCreateDto) {
        Plano novoPlano = planoService.registrarPlano(PlanoMapper.toPlano(planoCreateDto));

        return ResponseEntity.status(HttpStatus.CREATED).body(PlanoMapper.toPlanoDto(novoPlano));
    }

    @Override
    @GetMapping("/buscar")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<PlanoResponseDto>> listarPlanos() {

        List<Plano> listPlano = planoService.buscarPlanos();
        return ResponseEntity.ok(PlanoMapper.toPlanoDtoList(listPlano));
    }

    @Override
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlano(@PathVariable Long id) {
        planoService.deletePlano(id);
        return  ResponseEntity.noContent().build();
    }
    @Override
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<PlanoResponseDto> buscarPlanoByid(@PathVariable Long id) {
        Plano plano = planoService.buscarPlanoById(id);

        return ResponseEntity.ok(PlanoMapper.toPlanoDto(plano));
    }
    @Override
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanoResponseDto> editarPlano(
            @PathVariable Long id,@RequestBody @Valid PlanoUpdateDto updateDto){

        PlanoResponseDto planoAtulizado =  planoService.editarPlano(id,updateDto);
        return ResponseEntity.ok(planoAtulizado);
    }
    @PreAuthorize("permitAll()")
    @PatchMapping("/mudar/{usuarioId}")
    public ResponseEntity<Void> mudarPlano(
            @PathVariable Long usuarioId,
            @RequestBody @Valid PlanoTrocaDto dto) {

        planoService.mudarPlanoDoUsuario(usuarioId, dto.getNovoPlanoId());

        return ResponseEntity.noContent().build();
    }

}
