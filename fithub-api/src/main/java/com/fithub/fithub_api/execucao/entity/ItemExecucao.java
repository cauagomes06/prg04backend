package com.fithub.fithub_api.execucao.entity;

import com.fithub.fithub_api.exercicio.entity.Exercicio;
import com.fithub.fithub_api.infraestructure.entity.PersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "itens_execucoes")
@Getter
@Setter
public class ItemExecucao extends PersistenceEntity implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treino_execucao_id", nullable = false)
    private TreinoExecucao treinoExecucao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercicio_id", nullable = false)
    private Exercicio exercicio;

    @Column(name = "series_concluidas")
    private Integer seriesConcluidas;

    private String observacoes;
}
