package com.fithub.fithub_api.usuario.service;

import com.fithub.fithub_api.aula.dto.InstrutorResponseDto;
import com.fithub.fithub_api.usuario.entity.Usuario;

import com.fithub.fithub_api.usuario.dto.UsuarioCreateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface UsuarioIService {

    public Usuario registrarUsuario(Usuario usuario);

    Usuario editarSenha(Long id,String senhaAtual, String novaSenha,String confirmaSenha);

    Usuario buscarPorId(Long id);

    Page<Usuario> buscarTodos(Pageable pageable);

    void delete(Long id);

    Usuario buscarPorUsername(String username);

    List<InstrutorResponseDto> buscarInstrutores();

    void atualizarFoto(Long id, String novaUrl);

    void alterarPerfilUsuario(Long usuarioId, Long novoPerfilId);
}
