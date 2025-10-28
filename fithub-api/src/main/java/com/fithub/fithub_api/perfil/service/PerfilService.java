package com.fithub.fithub_api.perfil.service;

import com.fithub.fithub_api.exception.PerfilUniqueViolationException;
import com.fithub.fithub_api.perfil.entity.Perfil;
import com.fithub.fithub_api.perfil.repository.PerfilRepository;
import com.fithub.fithub_api.usuario.repository.UsuarioRepository;
import com.fithub.fithub_api.web.dto.PerfilResponseDto;
import com.fithub.fithub_api.web.dto.PerfilUpdateDto;
import com.fithub.fithub_api.web.dto.mapper.PerfilMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PerfilService  implements PerfilIService{

     private final   PerfilRepository perfilRepository;
     private final   UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public Perfil registrarPerfil(Perfil perfil) {
        try {
            return perfilRepository.save(perfil);
        }catch (DataIntegrityViolationException e){
            throw new PerfilUniqueViolationException(
                    String.format("Já existe um perfil com este nome",perfil.getNome()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Perfil> buscarPerfis() {
        return perfilRepository.findAll();
    }

    @Override
    public Perfil buscarPerfilByid(Long id) {

        return perfilRepository.findById(id).orElseThrow( () ->
                new RuntimeException ("Perfil não encontrado") );

    }

    @Override
    public void delete(Long id) {

        Perfil perfil = this.buscarPerfilByid(id);

        if(usuarioRepository.existsById(perfil.getId())){
            throw new IllegalStateException("Existem usuarios com este perfil,apague-os primeiro");
        }
        perfilRepository.delete(perfil);
    }

    @Override
    public PerfilResponseDto editarPerfil(Long id, PerfilUpdateDto updateDto) {

         Perfil perfilExistente = this.buscarPerfilByid(id);

         perfilExistente.setNome(updateDto.getNome());
         perfilExistente.setDescricao(updateDto.getDescricao());
         perfilRepository.save(perfilExistente);

        return PerfilMapper.toPerfilDto(perfilExistente);
    }
}
