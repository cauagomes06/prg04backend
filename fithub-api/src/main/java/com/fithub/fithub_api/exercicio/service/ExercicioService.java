package com.fithub.fithub_api.exercicio.service;

import com.fithub.fithub_api.exception.EntityNotFoundException;
import com.fithub.fithub_api.exercicio.entity.Exercicio;
import com.fithub.fithub_api.exercicio.repository.ExercicoRepository;
import com.fithub.fithub_api.itemtreino.repository.ItemTreinoRepository;
import com.fithub.fithub_api.exercicio.dto.ExercicioCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExercicioService implements ExercicioIService {

    private final ExercicoRepository exercicoRepository;
    private final ItemTreinoRepository itemTreinoRepository;

    @Override
    @Transactional(readOnly = true)
    public Exercicio buscarPorId(Long id) {
        return exercicoRepository.findById(id).orElseThrow
                (()-> new EntityNotFoundException("Exercico com ID : " +id+ "%d nao encontrado. ") );
    }

    @Override
    @Transactional
    public Exercicio criarExercicio(Exercicio exercicio) {

        try {
            return exercicoRepository.save(exercicio);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Exercicio com o nome "+ exercicio.getNome()+"ja existe");
        }
    }

    @Override
    @Transactional
    public void deletarExercicio(Long id) {

      Exercicio exercicio =  buscarPorId(id);

      if (itemTreinoRepository.existsByExercicioId(exercicio.getId())) {
          throw new IllegalStateException("ainda existem treinos com este exercicio");
      }
      exercicoRepository.delete(exercicio);
    }

    @Override
    @Transactional
    public Exercicio editarExercicio(Long id,ExercicioCreateDto updateDto) {//reutilizando o createDto aqui

            Exercicio exercicioExistente =  this.buscarPorId(id);

            exercicioExistente.setNome(updateDto.getNome());
            exercicioExistente.setGrupoMuscular(updateDto.getGrupoMuscular());
            exercicioExistente.setDescricao(updateDto.getDescricao());
            exercicioExistente.setUrlVideo(updateDto.getUrlVideo());

            try {
                return exercicoRepository.save(exercicioExistente);
            }catch (DataIntegrityViolationException e) {
                throw new RuntimeException("Erro ao atualizar: nome de exercicio ja existe "+updateDto.getNome());
            }

    }


    @Override
    @Transactional(readOnly = true)
    public Page<Exercicio> buscarTodos(Pageable pageable) {
        return exercicoRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exercicio> buscarPorGrupoMuscular(String nomeMusculo) {

        return exercicoRepository.findByGrupoMuscularContainingIgnoreCase(nomeMusculo);
    }
}
