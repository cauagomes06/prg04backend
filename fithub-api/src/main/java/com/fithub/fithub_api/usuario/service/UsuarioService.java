package com.fithub.fithub_api.usuario.service;

import com.fithub.fithub_api.aula.dto.InstrutorResponseDto;
import com.fithub.fithub_api.exception.EntityNotFoundException;
import com.fithub.fithub_api.gamificacao.service.GamificacaoIService;
import com.fithub.fithub_api.infraestructure.service.FileBucketService;
import com.fithub.fithub_api.pessoa.repository.PessoaRepository;
import com.fithub.fithub_api.plano.entity.Plano;
import com.fithub.fithub_api.plano.repository.PlanoRepository;
import com.fithub.fithub_api.usuario.dto.UsuarioPerfilPublicoDto;
import com.fithub.fithub_api.usuario.entity.StatusPlano;
import com.fithub.fithub_api.usuario.exception.CpfUniqueViolationException;
import com.fithub.fithub_api.usuario.exception.PasswordInvalidException;
import com.fithub.fithub_api.usuario.exception.UsernameUniqueViolationException;
import com.fithub.fithub_api.perfil.entity.Perfil;
import com.fithub.fithub_api.perfil.repository.PerfilRepository;
import com.fithub.fithub_api.usuario.dto.UsuarioRankingDto;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.repository.UsuarioRepository;
import com.fithub.fithub_api.usuario.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UsuarioIService, UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PessoaRepository pessoaRepository;
    private final PlanoRepository planoRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileBucketService fileBucketService;
    private final GamificacaoIService  gamificacaoIService;

    @Override
    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new UsernameUniqueViolationException(String.format("Usuario %s já cadastrado", usuario.getUsername()));
        }
        if (pessoaRepository.existsByCpf(usuario.getPessoa().getCpf())) {
            throw new CpfUniqueViolationException(
                    "Ja existe um usuario registrado com o cpf : " + usuario.getPessoa().getCpf());
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario editarSenha(Long id, String senhaAtual, String novaSenha, String confirmaSenha) {
        if (!novaSenha.equals(confirmaSenha)) {
            throw new PasswordInvalidException("Nova senha nao confere com confirmação de senha");
        }

        Usuario user = buscarPorId(id);
        if (!passwordEncoder.matches(senhaAtual, user.getPassword())) {
            throw new PasswordInvalidException("sua senha não confere");
        }
        user.setPassword(passwordEncoder.encode(novaSenha));
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuario com id %s não encontrado", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Usuario> buscarTodos(Pageable pageable, String search, String perfil) {

        // 1. Trata o filtro de Perfil
        String filtroPerfil = "";
        // Ignora se for nulo, vazio, ou a palavra "Todos" (independente de maiúscula/minúscula)
        if (perfil != null && !perfil.trim().isEmpty() && !perfil.equalsIgnoreCase("TODOS")) {
            // Força para maiúsculo para bater com o padrão do banco de dados
            filtroPerfil = perfil.toUpperCase();
        }

        // 2. Trata a Busca por Texto
        String termoBusca = (search != null && !search.isBlank()) ? search : "";

        // 3. Executa a query única
        return usuarioRepository.findWithFilters(termoBusca, filtroPerfil, pageable);
    }
    @Override
    @Transactional
    public void delete(Long id) {
        Usuario usuario = this.buscarPorId(id);
        usuarioRepository.delete(usuario);
    }

    // --- IMPLEMENTAÇÃO DO USER DETAILS SERVICE ---

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // AQUI ESTÁ A MUDANÇA CRÍTICA:
        // Retornamos o próprio objeto Usuario (que deve implementar UserDetails).
        // Isso evita ter que buscar o usuário novamente no Controller.
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario não encontrado com username: " + username));
    }

    // ---------------------------------------------

    @Transactional(readOnly = true)
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com username: " + username));
    }

    @Transactional(readOnly = true)
    public Page<UsuarioRankingDto> getRankingGeral(Pageable pageable) {
        Page<Usuario> usuariosPage = usuarioRepository.findAllByOrderByScoreTotalDesc(pageable);
        Page<UsuarioRankingDto> rankingPage = usuariosPage.map(UsuarioMapper::toRankingDto);

        long offset = pageable.getOffset();
        int posicaoAtual = (int) offset + 1;

        for (UsuarioRankingDto dto : rankingPage.getContent()) {
            dto.setPosicao(posicaoAtual++);
        }

        return rankingPage;
    }

    @Transactional
    public void alterarPerfilUsuario(Long usuarioId, Long novoPerfilId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + usuarioId));

        Perfil novoPerfil = perfilRepository.findById(novoPerfilId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil não encontrado com id: " + novoPerfilId));

        usuario.setPerfil(novoPerfil);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstrutorResponseDto> buscarInstrutores() {
        List<Usuario> instrutores = usuarioRepository.findAllByPerfilNome("ROLE_PERSONAL");
        return UsuarioMapper.toListInstrutorDto(instrutores);
    }
    @Override
    @Transactional
    public String atualizarFoto(Long id, MultipartFile arquivo) {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Arquivo inválido.");
        }

        Usuario usuario = buscarPorId(id);

        try {

            String extensao = arquivo.getOriginalFilename()
                    .substring(arquivo.getOriginalFilename().lastIndexOf("."));

            String fileName = "usuarios/" + id + extensao;

            byte[] content = arquivo.getBytes();

            String url = fileBucketService.uploadFile(fileName, content);

            usuario.setFotoUrl(url);

            usuarioRepository.save(usuario);

            return url;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao fazer upload da foto.", e);
        }
    }

    @Override
    @Transactional
    public void atualizarPlanoUsuario(Long usuarioId, Long novoPlanoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Plano novoPlano = planoRepository.findById(novoPlanoId)
                .orElseThrow(() -> new EntityNotFoundException("Plano não encontrado"));

        usuario.setPlano(novoPlano);
        usuario.setStatusPlano(StatusPlano.ATIVO);
        usuario.setDataVencimentoPlano(LocalDate.now().plusDays(30));

        usuarioRepository.save(usuario);
    }
    @Override
    @Transactional(readOnly = true)
    public UsuarioPerfilPublicoDto buscarPerfilPublico(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        // Extrai os dados dinâmicos de gamificação
        var progresso = gamificacaoIService.calcularProgresso(usuario.getScoreTotal());

        // Delega a conversão para o Mapper e retorna o DTO
        return UsuarioMapper.toPerfilPublicoDto(usuario, progresso.getNivel(), progresso.getTituloNivel());
    }
}