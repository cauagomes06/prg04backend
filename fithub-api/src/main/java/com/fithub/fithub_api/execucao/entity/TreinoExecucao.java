package com.fithub.fithub_api.execucao.entity;

import com.fithub.fithub_api.infraestructure.entity.PersistenceEntity;
import com.fithub.fithub_api.treino.entity.Treino;
import com.fithub.fithub_api.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "treinos_execucoes")
@Getter
@Setter
public class TreinoExecucao extends PersistenceEntity implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treino_id", nullable = false)
    private Treino treino;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    @Column(name = "duracao_segundos")
    private Long duracaoSegundos;

    @Column(name = "pontos_ganhos")
    private Integer pontosGanhos;

    @OneToMany(mappedBy = "treinoExecucao", cascade = CascadeType.ALL)
    private List<ItemExecucao> itens;
}
