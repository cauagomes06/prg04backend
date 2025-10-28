package com.fithub.fithub_api.plano.service;

import com.fithub.fithub_api.exception.PlanoUniqueViolationException;
import com.fithub.fithub_api.plano.entity.Plano;
import com.fithub.fithub_api.plano.repository.PlanoRepository;
import com.fithub.fithub_api.usuario.repository.UsuarioRepository;
import com.fithub.fithub_api.web.dto.PlanoResponseDto;
import com.fithub.fithub_api.web.dto.PlanoUpdateDto;
import com.fithub.fithub_api.web.dto.mapper.PlanoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanoService implements PlanoIService {

    private final PlanoRepository planoRepository;
    private final UsuarioRepository usuarioRepository;

        @Transactional
        @Override
        public Plano registrarPlano(Plano plano) {

            try {
               return planoRepository.save(plano);
            } catch (DataIntegrityViolationException e) {
                throw new PlanoUniqueViolationException
                        (String.format("Ja existe um plano com este nome %s",plano.getNome()));
            }
        }

    @Override
    @Transactional(readOnly = true)
    public List<Plano> buscarPlanos() {

        return planoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Plano buscarPlanoById(Long id) {
        return planoRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("Plano não encontrado"));
    }

    @Override
    @Transactional
    public void deletePlano(Long id) {

        Plano plano = this.buscarPlanoById(id);
        if(usuarioRepository.existsByPlanoId(plano.getId())){
            throw new IllegalStateException("Não é possivel excluir o plano,pois existem usuarios associados a ele");
        }
        planoRepository.deleteById(id);
    }
    @Transactional
    @Override
    public PlanoResponseDto editarPlano(Long id,PlanoUpdateDto updateDto) {

       Plano planoExistente =  this.buscarPlanoById(id);

       planoExistente.setNome(updateDto.getNome());
       planoExistente.setDescricao(updateDto.getDescricao());
       planoExistente.setPreco(updateDto.getPreco());

       planoRepository.save(planoExistente);

       return PlanoMapper.toPlanoDto(planoExistente);
    }


}
