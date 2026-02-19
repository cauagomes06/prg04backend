package com.fithub.fithub_api.plano.service;

import com.fithub.fithub_api.exception.EntityNotFoundException;
import com.fithub.fithub_api.infraestructure.exception.EntityEmUsoException;
import com.fithub.fithub_api.plano.exception.PlanoUniqueViolationException;
import com.fithub.fithub_api.plano.entity.Plano;
import com.fithub.fithub_api.plano.repository.PlanoRepository;
import com.fithub.fithub_api.usuario.entity.StatusPlano;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.repository.UsuarioRepository;
import com.fithub.fithub_api.plano.dto.PlanoResponseDto;
import com.fithub.fithub_api.plano.dto.PlanoUpdateDto;
import com.fithub.fithub_api.plano.mapper.PlanoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    public List<Plano> buscarPlanos(String search) {
            //se nao tiver nada conrespondente com a pesquisa,traz todos
        if (search == null || search.trim().isEmpty()) {
            return planoRepository.findAll();
        }
        return planoRepository.findByNomeContainingIgnoreCase(search);
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
            throw new EntityEmUsoException("Não é possivel excluir o plano,pois existem usuarios associados a ele");
        }
        planoRepository.deleteById(id);
    }
    @Transactional
    @Override
    public PlanoResponseDto editarPlano(Long id,PlanoUpdateDto updateDto) {

       Plano planoExistente =  this.buscarPlanoById(id);

       if(!planoRepository.existsByNome(updateDto.getNome())){

           planoExistente.setNome(updateDto.getNome());
           planoExistente.setDescricao(updateDto.getDescricao());
           planoExistente.setPreco(updateDto.getPreco());

           planoRepository.save(planoExistente);

       }else {
           throw new PlanoUniqueViolationException("Plano com este nome ja existe");
       }


       return PlanoMapper.toPlanoDto(planoExistente);
    }

    @Override
    @Transactional
    public void mudarPlanoDoUsuario(Long usuarioId, Long novoPlanoId) {
        // Busca o usuário
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        // Busca o novo plano
        Plano novoPlano = planoRepository.findById(novoPlanoId)
                .orElseThrow(() -> new EntityNotFoundException("Plano não encontrado"));

        // Atualiza o plano do usuário
        usuario.setPlano(novoPlano);

        // Salva a alteração
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void ativarPlano(Long usuarioId, Long planoId) {
        // 1. Busca o utilizador e o novo plano
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado para ativação."));

        Plano novoPlano = planoRepository.findById(planoId)
                .orElseThrow(() -> new EntityNotFoundException("Plano pago não encontrado no sistema."));

        // 2. Atualiza os dados
        usuario.setPlano(novoPlano);
        usuario.setStatusPlano(StatusPlano.ATIVO);

        // Define vencimento para daqui a 30 dias
        usuario.setDataVencimentoPlano(LocalDate.now().plusDays(30));

        // 3. Salva
        usuarioRepository.save(usuario);

        //  Log para debug
        System.out.println("PLANO ATIVADO: Usuário " + usuario.getUsername() + " agora é " + novoPlano.getNome());
    }

}
