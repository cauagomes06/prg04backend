package com.fithub.fithub_api.competicao.entity;

import com.fithub.fithub_api.infraestructure.entity.Auditable;
import com.fithub.fithub_api.infraestructure.entity.PersistenceEntity;
import com.fithub.fithub_api.inscricao.entity.Inscricao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "competicoes")
public class Competicao  extends PersistenceEntity implements Serializable {

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "descricao",length =500)
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime dataInicio;

    @Column(nullable = false)
    private LocalDateTime dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDeOrdenacao tipoOrdenacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCompeticao status;

    @Column(name = "pontos_vitoria", nullable = false, columnDefinition = "int default 0")
    private int pontosVitoria = 0; // Pontos que o vencedor receberá

    @OneToMany(mappedBy = "competicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Inscricao> inscricoes = new HashSet<>();

}
