package com.fithub.fithub_api.conquista.repository;

import com.fithub.fithub_api.conquista.entity.Conquista;
import com.fithub.fithub_api.conquista.enums.TipoMetrica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConquistaRepository extends JpaRepository<Conquista, Long> {

    Optional<Conquista> findByChaveTecnica(String chaveTecnica);

    // Busca todas as conquistas baseadas em uma métrica específica
    List<Conquista> findByTipoMetrica(TipoMetrica tipoMetrica);
}