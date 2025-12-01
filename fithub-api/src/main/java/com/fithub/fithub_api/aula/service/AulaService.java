package com.fithub.fithub_api.aula.service;

import com.fithub.fithub_api.aula.dto.ParticipanteDto;
import com.fithub.fithub_api.aula.entity.Aula;
import com.fithub.fithub_api.aula.repository.AulaRepository;
import com.fithub.fithub_api.aula.exception.AulaConflictException;
import com.fithub.fithub_api.exception.EntityNotFoundException;

import com.fithub.fithub_api.reserva.mapper.ReservaMapper;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.usuario.service.UsuarioIService;
import com.fithub.fithub_api.aula.dto.AulaCreateDto;
import com.fithub.fithub_api.aula.dto.AulaUpdateDto;
import com.fithub.fithub_api.aula.mapper.AulaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AulaService implements AulaIService {

    private final UsuarioIService usuarioIService;
    private final AulaRepository aulaRepository;
    @Override
    @Transactional
    public Aula create(AulaCreateDto aulaCreateDto ) {

        Aula novaAula = AulaMapper.toAula(aulaCreateDto); // o mapeamento do instrutor nao estava conseguindo converter o id do dto para um usuario do objeto aula,tive que fazer separadamente

        Usuario instrutor = usuarioIService.buscarPorId(aulaCreateDto.getInstrutorIdentificador());
        novaAula.setInstrutor(instrutor);
        try {
            return aulaRepository.save(novaAula);
        }catch (DataIntegrityViolationException e) {
            throw new AulaConflictException( "Aula already exists" );

        }
    }

    @Override
    @Transactional
    public void delete(Long id) {

        Aula aula = this.buscarPorId(id);
        if(aula != null){
            aulaRepository.delete(aula);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Aula buscarPorId(Long id) {
        return aulaRepository.findById(id).orElseThrow
                (()-> new EntityNotFoundException("Aula com ID : " +id+ "%d nao encontrado. ") );
    }

    @Override
    @Transactional
    public Aula editarAula(Long id, AulaUpdateDto updateDto, Usuario usuarioLogado) {


        Aula aulaExistente = this.buscarPorId(id);

        //VERIFICAÇÃO DE PERMISSÃO
        // Apenas o dono (instrutor) da aula ou um ADMIN podem editar
        boolean isOwner = aulaExistente.getInstrutor().getId().equals(usuarioLogado.getId());
        boolean isAdmin = usuarioLogado.getPerfil().getNome().equals("ROLE_PERSONAL");

        if (!isOwner && !isAdmin) {
            log.info("Usuario nao tem permissao para editar aula");
            throw new AccessDeniedException("Você não tem permissão para editar esta aula.");

        }

        //Outra forma que seria possivel era utilizando varios ifs,mas achei que desta forma ficou mais organizado e menos poluido
        // Para cada campo, verifica se ele foi enviado no DTO e o atualiza.
        Optional.ofNullable(updateDto.getNome()).ifPresent(aulaExistente::setNome);
        Optional.ofNullable(updateDto.getDescricao()).ifPresent(aulaExistente::setDescricao);
        Optional.ofNullable(updateDto.getDataHoraInicio()).ifPresent(aulaExistente::setDataHoraInicio);
        Optional.ofNullable(updateDto.getDuracaoMinutos()).ifPresent(aulaExistente::setDuracaoMinutos);
        Optional.ofNullable(updateDto.getVagasTotais()).ifPresent(aulaExistente::setVagasTotais);


        if (updateDto.getInstrutorIdentificador() != null) {
            // Busca o novo instrutor
            Usuario novoInstrutor = usuarioIService.buscarPorId(updateDto.getInstrutorIdentificador());

            // se o novoInstrutor é ROLE_PERSONAL
            if (!novoInstrutor.getPerfil().getNome().equals("ROLE_PERSONAL")) {
                throw new IllegalArgumentException("O usuário selecionado não é um instrutor.");
            }
            aulaExistente.setInstrutor(novoInstrutor);
        }


        return aulaRepository.save(aulaExistente);
    }
    @Override
    @Transactional(readOnly = true)
    public List<ParticipanteDto> buscarParticipantes(Long aulaId, Usuario usuarioLogado) {

        Aula aula = buscarPorId(aulaId);

        return ReservaMapper.toParticipanteListDto(aula.getReservas());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Aula> buscarAulasComFiltro(Integer ano, Integer mes, Long instrutorId) {

            if (mes != null && ano != null) {
                return aulaRepository.findByAnoEMes(ano,mes);
            }
            if (instrutorId != null) {
                return aulaRepository.findByInstrutorId(instrutorId);
            }
        return aulaRepository.findAll();
    }
}
