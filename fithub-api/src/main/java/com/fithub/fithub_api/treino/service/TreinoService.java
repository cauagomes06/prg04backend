package com.fithub.fithub_api.treino.service;

import com.fithub.fithub_api.exception.EntityNotFoundException;
import com.fithub.fithub_api.exercicio.entity.Exercicio;
import com.fithub.fithub_api.exercicio.service.ExercicioService;
import com.fithub.fithub_api.itemtreino.entity.ItemTreino;
import com.fithub.fithub_api.treino.entity.StatusTreino;
import com.fithub.fithub_api.treino.entity.Treino;
import com.fithub.fithub_api.treino.repository.TreinoRepository;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.web.dto.ItemTreinoCreateDto;
import com.fithub.fithub_api.web.dto.TreinoCreateDto;
import com.fithub.fithub_api.web.dto.mapper.TreinoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TreinoService  implements TreinoIService{

    private final TreinoRepository treinoRepository;
    private final ExercicioService exercicioService;

    @Override
    @Transactional
    public Treino criarTreino(TreinoCreateDto dto, Usuario criador) {

        Treino treino = TreinoMapper.toTreino(dto);
        treino.setCriador(criador);
        treino.setStatus(StatusTreino.PRIVADO);

        List<ItemTreino> items = new ArrayList<>();

        if(dto.getItems() != null) {
            for (ItemTreinoCreateDto item : dto.getItems()) {

                Exercicio exercicio = exercicioService.buscarPorId(item.getExercicioId());
                ItemTreino itemTreino = new ItemTreino();
                itemTreino.setExercicio(exercicio);
                itemTreino.setTreino(treino);
                itemTreino.setSeries(item.getSeries());
                itemTreino.setDescanso(item.getDescanso());
                itemTreino.setOrdem(item.getOrdem());
                itemTreino.setRepeticoes(item.getRepeticoes());

                items.add(itemTreino);
            }
        }
        treino.setItensTreino(items);
        return treinoRepository.save(treino);
    }

    @Transactional
    public void deletarTreino(Long idTreino, Usuario usuarioLogado) {
        Treino treino = buscarTreinoPorId(idTreino);

        // VERIFICAÇÃO DE POSSE
        if (!treino.getCriador().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Você não tem permissão para apagar este treino.");
        }

        treinoRepository.delete(treino);
    }

    @Transactional(readOnly = true)
    public Treino buscarTreinoPorId(Long id) {
        return treinoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Treino com ID #" + id + " não encontrado."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Treino> buscarTodos() {
        return treinoRepository.findAll();
    }

    @Override
    public Treino editarTreino(TreinoCreateDto dto, Usuario usuarioLogado, Long id) {

        Treino treino = buscarTreinoPorId(id);

        if(!treino.getCriador().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Voce nao tem permissao para editar o treino.");
        }

        treino.setNome(dto.getNome());
        treino.setDescricao(dto.getDescricao());

        if (dto.getItems() != null) {
            for (ItemTreinoCreateDto item : dto.getItems()) {

                Exercicio exercicio = exercicioService.buscarPorId(item.getExercicioId());
                ItemTreino itemTreino = new ItemTreino();
                itemTreino.setExercicio(exercicio);
                itemTreino.setTreino(treino);
                itemTreino.setSeries(item.getSeries());
                itemTreino.setDescanso(item.getDescanso());
                itemTreino.setOrdem(item.getOrdem());
                itemTreino.setRepeticoes(item.getRepeticoes());

                treino.getItensTreino().add(itemTreino);
            }
        }

        return treinoRepository.save(treino);
    }

    @Transactional
    public Treino publicarTreino(Long idTreino, Usuario usuarioLogado) {
        Treino treino = buscarTreinoPorId(idTreino);

        // VERIFICAÇÃO DE POSSE
        if (!treino.getCriador().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Você não tem permissão para publicar este treino.");
        }

        // VERIFICAÇÃO DE PERFIL
        if (!"ROLE_PERSONAL".equals(usuarioLogado.getPerfil().getNome())) {
            throw new AccessDeniedException("Apenas personais podem publicar treinos.");
        }

        treino.setStatus(StatusTreino.PUBLICADO);
        return treinoRepository.save(treino);
    }
}
