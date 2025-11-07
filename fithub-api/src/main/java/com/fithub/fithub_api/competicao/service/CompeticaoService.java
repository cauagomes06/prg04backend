package com.fithub.fithub_api.competicao.service;

import com.fithub.fithub_api.competicao.entity.Competicao;
import com.fithub.fithub_api.competicao.entity.StatusCompeticao;
import com.fithub.fithub_api.competicao.repository.CompeticaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CompeticaoService implements CompeticaoIService{

    private final CompeticaoRepository competicoRepository;

    @Override
    @Transactional
    public Competicao create(Competicao competicao) {

        try{
            competicao.setStatus(StatusCompeticao.ABERTA);
            return competicoRepository.save(competicao);
        }catch (DataIntegrityViolationException ex){
            throw new RuntimeException("ja existe uma competicao com o nome:" +competicao.getNome());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<Competicao> listarCompeticao() {
        return competicoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Competicao buscarPorId(Long id) {
        return competicoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Competicao nao encontrada"));
    }

    @Override
    @Transactional
    public void deletarCompeticao(Long id) {

        Competicao competico = buscarPorId(id);
        competicoRepository.delete(competico);
    }

    @Override
    @Transactional
    public Competicao editarCompeticao(Competicao competicaoDadosNovos,Long id) {


        Competicao competicaoParaEditar = this.buscarPorId(id); // neste metodo ja tem verificções caso o id seja invalido


        //preenche com os dados novos
        competicaoParaEditar.setNome(competicaoDadosNovos.getNome());
        competicaoParaEditar.setDescricao(competicaoDadosNovos.getDescricao());
        competicaoParaEditar.setDataInicio(competicaoDadosNovos.getDataInicio());
        competicaoParaEditar.setDataFim(competicaoDadosNovos.getDataFim());
        competicaoParaEditar.setPontosVitoria(competicaoDadosNovos.getPontosVitoria());
        competicaoParaEditar.setStatus(StatusCompeticao.ABERTA);

        //retorna salvando
        return competicoRepository.save(competicaoParaEditar);
    }
}
