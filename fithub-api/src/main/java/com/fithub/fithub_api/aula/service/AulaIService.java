package com.fithub.fithub_api.aula.service;

import com.fithub.fithub_api.aula.entity.Aula;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.aula.dto.AulaCreateDto;
import com.fithub.fithub_api.aula.dto.AulaUpdateDto;

import java.util.List;

public interface AulaIService {

    Aula create( AulaCreateDto aulaCreateDto);

    void delete(Long id);

    Aula buscarPorId(Long id);


    Aula editarAula(Long id, AulaUpdateDto updateDto, Usuario usuarioLogado);

    List<Aula> buscarAulasComFiltro(Integer ano, Integer mes, Long instrutorId);
}
