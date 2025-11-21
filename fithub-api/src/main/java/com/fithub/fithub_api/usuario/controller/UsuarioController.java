package com.fithub.fithub_api.usuario.controller;

import com.fithub.fithub_api.pessoa.dto.PessoaUpdateDto;
import com.fithub.fithub_api.pessoa.service.PessoaService;
import com.fithub.fithub_api.usuario.dto.UsuarioRankingDto;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.service.UsuarioService;
import com.fithub.fithub_api.usuario.dto.UsuarioCreateDto;
import com.fithub.fithub_api.usuario.dto.UsuarioResponseDto;
import com.fithub.fithub_api.usuario.dto.UsuarioSenhaDto;
import com.fithub.fithub_api.usuario.mapper.UsuarioMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController implements UsuarioIController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final PessoaService pessoaService;

    @Override
    @PostMapping("/register")
    // Endpoint público (geralmente configurado no SecurityConfig para permitAll)
    public ResponseEntity<UsuarioResponseDto> registrarUsuario(@Valid @RequestBody UsuarioCreateDto createDto) {
        Usuario usuarioSalvo = usuarioService.registrarUsuario(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioMapper.toDto(usuarioSalvo));
    }

    @Override
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // 🔒 MELHORIA: Só admin lista todos
    public ResponseEntity<List<UsuarioResponseDto>> buscaTodos(){
        List<Usuario> usuarios = usuarioService.buscarTodos();
        return ResponseEntity.ok(usuarioMapper.toListDto(usuarios));
    }

    @Override
    @GetMapping("/{id}")
    // 🔒 MELHORIA: Só Admin ou o próprio usuário podem ver os detalhes completos por ID
    // Obs: Isso exige que o Spring Security consiga ler o ID do principal, ou você confia no Service.
    // Simplificação segura: Apenas Admin usa este endpoint. O usuário comum usa o /me.
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDto> buscarPorId(@PathVariable Long id) {
        Usuario user = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuarioMapper.toDto(user));
    }

    @Override
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDto> deleteUsuario(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
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

    @Override
    @GetMapping("/ranking")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UsuarioRankingDto>> getRankingGeral() {
        List<UsuarioRankingDto> ranking = usuarioService.getRankingGeral();
        return ResponseEntity.ok(ranking);
    }

    @Override
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

    @Override
    @PatchMapping("/me/foto")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> atualizarFotoPerfil(
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserDetails userDetails) {

        String novaUrl = payload.get("fotoUrl");
        Usuario usuario = getUsuarioEntity(userDetails); // Uso do método auxiliar

        usuarioService.atualizarFoto(usuario.getId(), novaUrl);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping("/{id}/alterar-perfil")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> atualizarPerfil(
            @PathVariable Long id,
            @RequestParam Long novoPerfilId) {

        usuarioService.alterarPerfilUsuario(id, novoPerfilId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDto> getUsuarioLogado(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();

        Usuario usuario = getUsuarioEntity(userDetails);
        return ResponseEntity.ok(usuarioMapper.toDto(usuario));
    }

    // --- MÉTODOS AUXILIARES ---

    private Usuario getUsuarioEntity(UserDetails userDetails) {
        return usuarioService.buscarPorUsername(userDetails.getUsername());
    }

    private boolean isUserAdmin(Usuario usuario) {
        return usuario.getPerfil().getNome().equals("ROLE_ADMIN");
    }
}