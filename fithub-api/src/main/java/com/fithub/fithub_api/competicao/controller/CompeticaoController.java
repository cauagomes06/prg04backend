package com.fithub.fithub_api.competicao.controller;


import com.fithub.fithub_api.competicao.dto.CompeticaoCreateDto;
import com.fithub.fithub_api.competicao.dto.CompeticaoResponseDto;
import com.fithub.fithub_api.competicao.entity.Competicao;
import com.fithub.fithub_api.competicao.mapper.CompeticaoMapper;
import com.fithub.fithub_api.competicao.service.CompeticaoService;
import com.fithub.fithub_api.inscricao.dto.InscricaoResponseDto;
import com.fithub.fithub_api.inscricao.entity.Inscricao;
import com.fithub.fithub_api.inscricao.mapper.InscricaoMapper;
import com.fithub.fithub_api.inscricao.service.InscricaoService;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("api/competicoes")
public class CompeticaoController implements CompeticaoIController{

    private final CompeticaoService competicoService;
    private final InscricaoService inscricaoService;
    private final UsuarioService usuarioService;


    @Override
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'PERSONAL')")
    public ResponseEntity<CompeticaoResponseDto> criarCompeticao(@RequestBody @Valid CompeticaoCreateDto createDto) {

        Competicao competicao = competicoService.create(CompeticaoMapper.toCompeticao(createDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(CompeticaoMapper.toDto(competicao));
    }

    @Override
    @GetMapping("/buscar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CompeticaoResponseDto>> listarCompeticao() {

        List<Competicao> competicoes = competicoService.listarCompeticao();
        return ResponseEntity.ok().body(CompeticaoMapper.toListDto(competicoes));
    }

    @Override
    @GetMapping("/buscar/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CompeticaoResponseDto> buscarPorId(@PathVariable Long id) {

            Competicao competicao = competicoService.buscarPorId(id);
        return ResponseEntity.ok().body(CompeticaoMapper.toDto(competicao));
    }

    @Override
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarCompeticao(@PathVariable Long id) {
            competicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PERSONAL')")
    public ResponseEntity<CompeticaoResponseDto> editarCompeticao(@RequestBody CompeticaoCreateDto updateDto,
                                                                  @PathVariable Long id) {

         Competicao competicaoModificada = competicoService.editarCompeticao(
                 CompeticaoMapper.toCompeticao(updateDto),id);
        return ResponseEntity.ok().body(CompeticaoMapper.toDto(competicaoModificada));
    }

    @Override
    @PostMapping("/{id}/inscrever")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InscricaoResponseDto> inscrever(@PathVariable Long id,
                                                         @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuarioLogado = getUsuarioLogado(userDetails);

        Inscricao novaInscricao = inscricaoService.inscreverEmCompeticao(id,usuarioLogado);

        return ResponseEntity.status(HttpStatus.CREATED).body(InscricaoMapper.toDto(novaInscricao));

    }

    @Override
    @GetMapping("/{id}/ranking")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<InscricaoResponseDto>> getRankingDaCompeticao(
            @PathVariable Long id) {

        //retorna uma lista de todas as inscricoes ja na ordem de desempenho dos participantes
        List<Inscricao> inscricoesOrdenadas = inscricaoService.buscarInscricoesPorCompeticaoOrdenado(id);


        List<InscricaoResponseDto> ranking = InscricaoMapper.toListDto(inscricoesOrdenadas);

        return ResponseEntity.ok().body(ranking);
    }

    private Usuario getUsuarioLogado(UserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("Utilizador não autenticado.");
        }
        return usuarioService.buscarPorUsername(userDetails.getUsername());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'PERSONAL')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable Long id,
            @RequestParam("status") String novoStatus) {

        // Chama o serviço passando o ID e o status (convertendo string para Enum se necessário)
        competicoService.atualizarStatus(id, novoStatus);
        return ResponseEntity.noContent().build();
    }


}
