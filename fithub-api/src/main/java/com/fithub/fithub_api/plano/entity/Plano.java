package com.fithub.fithub_api.plano.entity;

import com.fithub.fithub_api.infraestructure.entity.Auditable;
import com.fithub.fithub_api.infraestructure.entity.PersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "planos")
public class Plano  extends PersistenceEntity implements Serializable {


    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "preco", nullable = false)
    private BigDecimal preco;
}
