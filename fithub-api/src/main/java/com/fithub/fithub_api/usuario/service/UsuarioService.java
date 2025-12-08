package com.fithub.fithub_api.usuario.service;

import com.fithub.fithub_api.aula.dto.InstrutorResponseDto;
import com.fithub.fithub_api.exception.EntityNotFoundException;
import com.fithub.fithub_api.pessoa.repository.PessoaRepository;
import com.fithub.fithub_api.usuario.exception.CpfUniqueViolationException;
import com.fithub.fithub_api.usuario.exception.PasswordInvalidException;
import com.fithub.fithub_api.usuario.exception.UsernameUniqueViolationException;
import com.fithub.fithub_api.perfil.entity.Perfil;
import com.fithub.fithub_api.perfil.repository.PerfilRepository;
import com.fithub.fithub_api.usuario.dto.UsuarioRankingDto;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.repository.UsuarioRepository;
import com.fithub.fithub_api.usuario.dto.UsuarioCreateDto;
import com.fithub.fithub_api.usuario.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService implements  UsuarioIService, UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PessoaRepository pessoaRepository;

    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario
                .getUsername())) {
            throw new UsernameUniqueViolationException(String.format("Usuario %s já cadastrado", usuario.getUsername()));
        }
        if(pessoaRepository.existsByCpf(usuario.getPessoa().getCpf())) {
            throw new CpfUniqueViolationException(
                    "Ja existe um usuario registrado com o cpf : "+usuario.getPessoa().getCpf());
        }


        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario editarSenha(Long id, String senhaAtual, String novaSenha, String confirmaSenha) {

            if(!novaSenha.equals(confirmaSenha)){
                throw  new PasswordInvalidException("Nova senha nao confere com confirmação de senha");
            }

            Usuario user = buscarPorId(id);
            if( !passwordEncoder.matches(senhaAtual,user.getPassword())){

                throw new PasswordInvalidException("sua senha não confere");
            }
            user.setPassword(passwordEncoder.encode(novaSenha));
            return user;
    }
    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuario com id %s  não encontrado", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Usuario> buscarTodos(Pageable pageable) {

        return usuarioRepository.findAll(pageable);
    }

    @Override
    public void delete(Long id) {

        Usuario usuario = this.buscarPorId(id);
        if(usuarioRepository.existsByUsername(usuario.getUsername())){
            usuarioRepository.delete(usuario);
        }
        else{
            throw new RuntimeException("Usuario não encontrado");
        }
    }


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("Usuario não encontrado com email:" +username));


        List<GrantedAuthority> authorities = new ArrayList<>();
        if (usuario.getPerfil() != null) {
            authorities.add(new SimpleGrantedAuthority((usuario.getPerfil().getNome())));
        }
                return new User(
                        usuario.getUsername(),
                        usuario.getPassword(),
                        authorities) ;
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email:" + username));
    }

    @Transactional(readOnly = true)
    public List<UsuarioRankingDto> getRankingGeral() {
        //busca os usuarios ordenados
        List<Usuario> usuariosOrdenados = usuarioRepository.findTop20ByOrderByScoreTotalDesc();

        //  Mapeia para DTOs
        List<UsuarioRankingDto> rankingDtos = UsuarioMapper.toRankingListDto(usuariosOrdenados);

        //  Adiciona a lógica da Posição (Rank)
        int posicao = 1;
        for (UsuarioRankingDto dto : rankingDtos) {
            dto.setPosicao(posicao++);
        }
        return rankingDtos;
    }
    @Transactional
    public void alterarPerfilUsuario(Long usuarioId, Long novoPerfilId) {
        // 1. Buscar o usuário
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + usuarioId));

        // 2. Buscar o novo perfil desejado
        Perfil novoPerfil = perfilRepository.findById(novoPerfilId)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado com id: " + novoPerfilId));

        // 3. Atualizar o perfil
        usuario.setPerfil(novoPerfil);

        usuarioRepository.save(usuario);
    }
    @Override
    public List<InstrutorResponseDto> buscarInstrutores() {
        // "INSTRUTOR" deve ser o nome exato do seu perfil no banco de dados.
        List<Usuario> instrutores = usuarioRepository.findAllByPerfilNome("ROLE_PERSONAL");
        return UsuarioMapper.toListInstrutorDto(instrutores);
    }

    @Override
    @Transactional
    public void atualizarFoto(Long id, String novaUrl) {
        Usuario usuario = buscarPorId(id);
        usuario.setFotoUrl(novaUrl);
        usuarioRepository.save(usuario);
    }
}
