package com.fithub.fithub_api.perfil.entity;

import com.fithub.fithub_api.infraestructure.entity.Auditable;
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
@Table(name = "perfis")
public class Perfil  extends PersistenceEntity implements Serializable {

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String descricao;


}
