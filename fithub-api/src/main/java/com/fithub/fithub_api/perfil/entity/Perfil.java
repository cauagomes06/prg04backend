package com.fithub.fithub_api.perfil.entity;

import com.fithub.fithub_api.infraestructure.entity.Auditable;
import com.fithub.fithub_api.infraestructure.entity.PersistenceEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@Entity
@Table(name = "perfis")
@AllArgsConstructor
public class Perfil  extends PersistenceEntity implements Serializable {

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String descricao;


}
