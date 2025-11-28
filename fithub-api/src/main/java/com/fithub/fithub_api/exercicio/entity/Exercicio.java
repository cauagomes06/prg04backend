package com.fithub.fithub_api.exercicio.entity;

import com.fithub.fithub_api.infraestructure.entity.PersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "exercicios")
public class Exercicio extends PersistenceEntity implements Serializable {


    @Column(nullable = false)
    private String nome;

    @Column
    private String descricao;

    @Column(nullable = false)
    private String grupoMuscular;

    @Column
    private String urlVideo;

}
