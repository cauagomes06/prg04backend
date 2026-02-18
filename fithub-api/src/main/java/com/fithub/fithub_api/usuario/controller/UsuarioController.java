package com.fithub.fithub_api.usuario.controller;

import com.fithub.fithub_api.pessoa.dto.PessoaUpdateDto;
import com.fithub.fithub_api.pessoa.service.PessoaService;
import com.fithub.fithub_api.usuario.dto.*;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.service.UsuarioService;
import com.fithub.fithub_api.usuario.mapper.UsuarioMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController  {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final PessoaService pessoaService;


    @PostMapping("/register")
    // Endpoint público (geralmente configurado no SecurityConfig para permitAll)
    public ResponseEntity<UsuarioResponseDto> registrarUsuario(@Valid @RequestBody UsuarioCreateDto createDto) {
        Usuario usuarioSalvo = usuarioService.registrarUsuario(usuarioMapper.toUsuario(createDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioMapper.toDto(usuarioSalvo));
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // 🔒 MELHORIA: Só admin lista todos
    public ResponseEntity<Page<UsuarioResponseDto>> buscaTodos(Pageable pageable){

        Page<Usuario> usuarioPage =usuarioService.buscarTodos(pageable);

        Page<UsuarioResponseDto> responseDtoPage = usuarioPage.map(usuarioMapper::toDto);
        return ResponseEntity.ok(responseDtoPage);
    }


    @GetMapping("/{id}")
    // 🔒 MELHORIA: Só Admin ou o próprio usuário podem ver os detalhes completos por ID
    // Obs: Isso exige que o Spring Security consiga ler o ID do principal, ou você confia no Service.
    // Simplificação segura: Apenas Admin usa este endpoint. O usuário comum usa o /me.
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDto> buscarPorId(@PathVariable Long id) {
        Usuario user = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuarioMapper.toDto(user));
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDto> deleteUsuario(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/update/senha/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateSenha(@PathVariable Long id,
                                            @Valid @RequestBody UsuarioSenhaDto senhaDto,
                                            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuarioLogado = getUsuarioEntity(userDetails);

        // Validação extra de segurança
        if (!usuarioLogado.getId().equals(id) && !isUserAdmin(usuarioLogado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        usuarioService.editarSenha(id, senhaDto.getSenhaAtual(), senhaDto.getNovaSenha(), senhaDto.getConfirmaSenha());
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/ranking")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<UsuarioRankingDto>> getRankingGeral(
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Page<UsuarioRankingDto> ranking = usuarioService.getRankingGeral(pageable);
        return ResponseEntity.ok(ranking);
    }


    @PutMapping("/me/dados-pessoais")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDto> atualizarDadosPessoais(
            @RequestBody @Valid PessoaUpdateDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuarioLogado = getUsuarioEntity(userDetails); // Uso do método auxiliar

        if (usuarioLogado.getPessoa() != null) {
            pessoaService.atualizar(usuarioLogado.getPessoa().getId(), dto);
        }

        return ResponseEntity.ok(usuarioMapper.toDto(usuarioLogado));
    }


    @PatchMapping(value = "/me/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // Indica que recebe arquivo
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> atualizarFotoPerfil(
            @RequestParam("file") MultipartFile file, // Recebe o arquivo do form-data
            @AuthenticationPrincipal Usuario usuarioLogado) {


        // Chama o serviço passando o arquivo real
        String novaUrl = usuarioService.atualizarFoto(usuarioLogado.getId(), file);

        // Retorna a nova URL para o React atualizar a imagem na hora sem F5
        return ResponseEntity.ok(Map.of("url", novaUrl));
    }


    @PatchMapping("/{id}/alterar-perfil")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> atualizarPerfil(
            @PathVariable Long id,
            @RequestParam Long novoPerfilId) {

        usuarioService.alterarPerfilUsuario(id, novoPerfilId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDto> getUsuarioLogado(@AuthenticationPrincipal Usuario usuarioLogado) {


        return ResponseEntity.ok(usuarioMapper.toDto(usuarioLogado));
    }

    @PatchMapping("/alterar-plano")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> alterarPlano(@RequestBody AlterarPlanoDto dto) {

        // Pegar o usuário logado do contexto de segurança
        Long usuarioLogadoId = recuperarIdUsuarioLogado();

        usuarioService.atualizarPlanoUsuario(usuarioLogadoId, dto.getPlanoId());

        return ResponseEntity.ok("Plano atualizado com sucesso!");
    }



    // --- MÉTODOS AUXILIARES ---

    private Usuario getUsuarioEntity(UserDetails userDetails) {
        return usuarioService.buscarPorUsername(userDetails.getUsername());
    }
    private Long recuperarIdUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return usuario.getId();
    }
    private boolean isUserAdmin(Usuario usuario) {
        return usuario.getPerfil().getNome().equals("ROLE_ADMIN");
    }
}