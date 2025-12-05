package com.fithub.fithub_api.pessoa.repository;

import com.fithub.fithub_api.pessoa.entity.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository  extends JpaRepository<Pessoa, Long> {

    boolean existsByCpf(String cpf);
}
