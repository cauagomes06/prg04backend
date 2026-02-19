package com.fithub.fithub_api.auth.controller;

import com.fithub.fithub_api.infraestructure.exception.PlanoDataViolation;
import com.fithub.fithub_api.plano.exception.PlanoUniqueViolationException;
import com.fithub.fithub_api.usuario.entity.StatusPlano;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioService usuarioService;


    public void verificarStatusPlanoPorUsername(String username ){

        Usuario usuario = usuarioService.buscarPorUsername(username);

        if(usuario.getPerfil().getNome().equals("ROLE_ADMIN")){
            return;
        }


        if (usuario.getStatusPlano() != StatusPlano.ATIVO){
            throw new PlanoDataViolation("Plano expirado");
        }
    }
}
