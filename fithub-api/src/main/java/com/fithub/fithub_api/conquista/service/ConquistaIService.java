package com.fithub.fithub_api.conquista.service;

import com.fithub.fithub_api.conquista.dto.ConquistaGaleriaDto;
import com.fithub.fithub_api.conquista.entity.Conquista;
import com.fithub.fithub_api.conquista.enums.TipoMetrica;
import com.fithub.fithub_api.usuario.entity.Usuario;

import java.util.List;

public interface ConquistaIService {

        List<Conquista> processarProgresso(Usuario usuario, TipoMetrica tipoMetrica, Double valorAcumulado);

        boolean concederMedalhaSegura(Usuario usuario, Conquista conquista);

        List<ConquistaGaleriaDto> obterGaleriaDoUsuario(Long usuarioId);
}
