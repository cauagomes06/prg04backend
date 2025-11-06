package com.fithub.fithub_api.usuario.service;

import com.fithub.fithub_api.usuario.entity.Usuario;

import com.fithub.fithub_api.usuario.dto.UsuarioCreateDto;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface UsuarioIService {

    public Usuario registrarUsuario(@RequestBody UsuarioCreateDto createDto);

    Usuario editarSenha(Long id,String senhaAtual, String novaSenha,String confirmaSenha);

    Usuario buscarPorId(long id);

    List<Usuario> buscarTodos();

    void delete(long id);

}
