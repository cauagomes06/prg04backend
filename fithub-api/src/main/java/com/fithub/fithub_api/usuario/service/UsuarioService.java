package com.fithub.fithub_api.usuario.service;

import com.fithub.fithub_api.exception.EntityNotFoundException;
import com.fithub.fithub_api.exception.PasswordInvalidException;
import com.fithub.fithub_api.exception.UsernameUniqueViolationException;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.repository.UsuarioRepository;
import com.fithub.fithub_api.web.dto.UsuarioCreateDto;
import com.fithub.fithub_api.web.dto.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public Usuario registrarUsuario(UsuarioCreateDto createDto) {
        if (usuarioRepository.existsByUsername(createDto.getUsername())) {
            throw new UsernameUniqueViolationException(String.format("Usuario %s já cadastrado", createDto.getUsername()));
        }

        Usuario novoUsuario = usuarioMapper.toUsuario(createDto);

        return usuarioRepository.save(novoUsuario);
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
    public Usuario buscarPorId(long id) {
        return usuarioRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuario com id %s  não encontrado", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> buscarTodos() {

        return usuarioRepository.findAll();
    }

    @Override
    public void delete(long id) {

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
}
