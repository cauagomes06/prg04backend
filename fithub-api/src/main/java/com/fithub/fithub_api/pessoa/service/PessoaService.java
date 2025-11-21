package com.fithub.fithub_api.pessoa.service;

import com.fithub.fithub_api.exception.EntityNotFoundException;
import com.fithub.fithub_api.pessoa.dto.PessoaUpdateDto;
import com.fithub.fithub_api.pessoa.entity.Pessoa;
import com.fithub.fithub_api.pessoa.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PessoaService implements PessoaIService{

    private final PessoaRepository pessoaRepository;

    // Método novo para atualizar dados
    @Transactional
    public Pessoa atualizar(Long id, PessoaUpdateDto dto) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dados pessoais não encontrados."));

        pessoa.setNomeCompleto(dto.getNomeCompleto());
        pessoa.setTelefone(dto.getTelefone());

        return pessoaRepository.save(pessoa);
    }
}
