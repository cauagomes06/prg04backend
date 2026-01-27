package com.fithub.fithub_api.competicao.service;

import com.fithub.fithub_api.competicao.entity.Competicao;
import com.fithub.fithub_api.competicao.entity.StatusCompeticao;
import com.fithub.fithub_api.competicao.mapper.CompeticaoMapper;
import com.fithub.fithub_api.competicao.repository.CompeticaoRepository;
import com.fithub.fithub_api.exception.EntityNotFoundException;
import com.fithub.fithub_api.inscricao.repository.InscricaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CompeticaoService implements CompeticaoIService{

    private final CompeticaoRepository competicoRepository;
    private final InscricaoRepository inscricaoRepository;

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
    @Transactional // Retirei o readOnly=true pois você faz save() dentro do método
    public Page<Competicao> listarCompeticao(Pageable pageable) {

        // Busca paginada direto do banco
        Page<Competicao> paginaCompeticoes = competicoRepository.findAll(pageable);

        LocalDateTime agora = LocalDateTime.now();

        // Itera apenas sobre o conteúdo da página atual para atualizar status
        for (Competicao comp : paginaCompeticoes.getContent()) {
            boolean alterou = false;

            // 1. ABERTA -> EM_ANDAMENTO
            if (comp.getStatus() == StatusCompeticao.ABERTA
                    && agora.isAfter(comp.getDataInicio())
                    && agora.isBefore(comp.getDataFim())) {

                comp.setStatus(StatusCompeticao.EM_ANDAMENTO);
                alterou = true;
            }

            // 2. ABERTA/EM_ANDAMENTO -> ENCERRADA
            if ((comp.getStatus() == StatusCompeticao.ABERTA || comp.getStatus() == StatusCompeticao.EM_ANDAMENTO)
                    && agora.isAfter(comp.getDataFim())) {

                comp.setStatus(StatusCompeticao.ENCERRADA);
                alterou = true;
            }

            if (alterou) {
                competicoRepository.save(comp);
            }
        }

        return paginaCompeticoes;
    }

    @Override
    @Transactional(readOnly = true)
    public Competicao buscarPorId(Long id) {
        return competicoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Competicao nao encontrada"));
    }

    @Override
    @Transactional
    public void deletar(Long id) {

        if (!competicoRepository.existsById(id)) {
            throw new EntityNotFoundException("Competição não encontrada com id: " + id);
        }
           // apaga todas as inscricoes vinculadas
        inscricaoRepository.deleteByCompeticaoId(id);

     // depois apaga todas as competicoes
        competicoRepository.deleteById(id);
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
    public void atualizarStatus(Long id, String novoStatusStr) {
        Competicao competicao = competicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competição não encontrada"));

        try {
            // Converte a String para o Enum (StatusCompeticao)
            // Certifique-se de que seu Enum tenha os valores: ABERTA, EM_ANDAMENTO, ENCERRADA, CANCELADA
            StatusCompeticao novoStatus = StatusCompeticao.valueOf(novoStatusStr.toUpperCase());

            competicao.setStatus(novoStatus);
            competicoRepository.save(competicao);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Status inválido: " + novoStatusStr);
        }
    }
}
